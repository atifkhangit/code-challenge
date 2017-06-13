/**
 * Created by Atif on 6/11/2017.
 */


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;

public class CustomerLTV {

    public static final String outputPath = "output/output.txt";
    private static final String inputPath = "input/input.txt";
    private static final String customer = "CUSTOMER";
    private static final String siteVisit = "SITE_VISIT";
    private static final String order = "ORDER";
    private static final String image = "IMAGE";
    private static final int lifeSpan = 10;
    private static HashMap<String, ArrayList<Order>> orderMap = new HashMap();
    private static HashMap<String, ArrayList<Image>> imageMap = new HashMap();
    private static HashMap<String, ArrayList<SiteVisit>> siteVisitMap = new HashMap();
    private static HashMap<String, ArrayList<Customer>> customerMap = new HashMap();
    private static HashMap<String, HashMap<String, ArrayList<Order>>> orders = new HashMap();
    private static HashMap<String, HashMap<String, ArrayList<Image>>> images = new HashMap();
    private static HashMap<String, HashMap<String, ArrayList<SiteVisit>>> siteVisits = new HashMap();
    private static HashMap<String, Customer> D = new HashMap();
    private static int totalWeeks;
    private static int totalVisits;
    private static float expenditureTotal;
    private static float siteVisitPerWeek;
    private static float expenditurePerVisit;
    private static int orderCount;

    /**
     * Calculate LTV value for the customer based customer_id.Updates the LTV for existing customers in D.
     * The function calculates basic variables for LTV like siteVisitPerWeek and expenditurePerVisit to be used
     * by formula SimpleLTV = 52 (siteVisitPerWeek * expenditurePerWeek) * lifeSpan of customer.
     *
     * @param customerId:
     */
    public static void calculateSimpleLTV(String customerId) {
        siteVisitPerWeek = 0;
        orderCount = 0;
        totalVisits = 0;
        totalWeeks = 0;
        expenditureTotal = 0;
        expenditurePerVisit = 0;
        if (siteVisits.containsKey(customerId)) {
            for (Map.Entry<String, ArrayList<SiteVisit>> e1 : siteVisits.get(customerId).entrySet()) {
                totalVisits = totalVisits + e1.getValue().size();
                totalWeeks = totalWeeks + 1;
            }
            siteVisitPerWeek = (totalVisits / totalWeeks);
        }
        if (orders.containsKey(customerId)) {
            for (Map.Entry<String, ArrayList<Order>> e2 : orders.get(customerId).entrySet()) {
                for (Order o : e2.getValue()) {
                    if (o.active == 1) {
                        expenditureTotal = expenditureTotal + o.getTotalAmount();
                        orderCount = orderCount + 1;
                    }
                }
            }
            expenditurePerVisit = (expenditureTotal / (float) orderCount);
        }

        double LTV = 52 * (expenditurePerVisit * siteVisitPerWeek) * lifeSpan;

        for (Customer c : customerMap.get(customerId)) {
            if (c.active == 1) {
                c.LTV = LTV;
                c.visitPerWeek = siteVisitPerWeek;
                c.revenuePerVisit = expenditurePerVisit;
                Customer cust = D.get(c.getKey());
                cust.LTV = LTV;
                cust.visitPerWeek = siteVisitPerWeek;
                cust.revenuePerVisit = expenditurePerVisit;
                D.put(c.getKey(), cust);
            }

        }
    }

    /**
     * TopXSimpleLTVCustomers returns the top x (number) customers with maximum LTV.
     *
     * @param x Number of customers
     * @param D Data structure storing latest customer records along with LTV value.
     *          D does not contain the historical customer records. All the latest and historical customer
     *          details can be retrieved from customerMap data structure.
     */
    public static void TopXSimpleLTVCustomers(int x, HashMap<String, Customer> D) {
        List<Customer> customerList = new ArrayList<Customer>(D.values());
        Collections.sort(customerList,
                (o1, o2) -> Double.compare(o2.LTV, o1.LTV));
        if (x <= customerList.size()) {
            customerList = customerList.subList(0, x);
            writeOutput(customerList);
        } else {
            writeOutput(customerList);
        }
    }

    /**
     * writeOutput is a utility function to write the top X customers with highest LTV.
     *
     * @param customerList List of all customers with latest information along with up to date LTV value.
     */
    public static void writeOutput(List<Customer> customerList) {
        try {
            FileWriter fw = new FileWriter(outputPath);
            BufferedWriter bw = new BufferedWriter(fw);
            DecimalFormat df = new DecimalFormat("#.##");
            for (Customer c : customerList) {
                bw.write("CustomerID: " + c.getKey() + "\t\t" + "LTV: $" + df.format(c.LTV) + "\t\t" +
                        "CustomerExpenditurePerVisit: $" + df.format(c.revenuePerVisit) + "\t\t" + "numberOfSiteVisitPerWeek: "
                        + c.visitPerWeek + "\n");
            }
            bw.close();
            fw.close();
        } catch (Exception er) {
            System.out.println(er.getMessage());
        }
    }

    /**
     * ingest function reads the input events and stores the latest customer information in data structure D.
     * The respective siteVisit, Orders, Images along with historical records for each customer can be obtained
     * from the respective orders,images,siteVisits maps which acts as virtual tables in memory.
     *
     * @param e event to be ingested.
     * @param D Data structure storing latest customer records along with LTV value.
     *          D does not contain the historical customer records. All the latest and historical customer
     *          details can be retrieved from customerMap data structure.
     */
    public static void ingest(JSONObject e, HashMap D) {
        if (e.getString("type").equalsIgnoreCase(customer)) {
            String customerId = e.getString("key");
            Customer c = new Customer();
            c.setVerb(e.getString("verb"));
            c.setKey(customerId);
            c.setEventTime(e.getString("event_time"));
            c.setLastName(e.getString("last_name"));
            c.setadrCity(e.getString("adr_city"));
            c.setadrState(e.getString("adr_state"));
            c.active = 1;

            /**
             * Ingest customer events.
             * Marks the old customer entry as inactive and set current entry as active entry if Update is received
             * Also pass the old LTV value from previous entry to new updated entry for customer.
             * This block updates customer details in data structure D and keeps the up to date customer entries.
             */
            if (customerMap.containsKey(customerId)) {
                ArrayList<Customer> temp = customerMap.get(customerId);
                if (e.getString("verb").equalsIgnoreCase("UPDATE")) {
                    for (Customer cust : temp) {
                        if (cust.active == 1) {
                            cust.active = 0;
                            c.LTV = cust.LTV;
                            c.visitPerWeek = cust.visitPerWeek;
                            c.revenuePerVisit = cust.revenuePerVisit;
                        }
                    }
                }
                temp.add(c);
                D.put(customerId, c);
            } else {
                ArrayList<Customer> temp2 = new ArrayList();
                temp2.add(c);
                customerMap.put(customerId, temp2);
                D.put(customerId, c);
            }
        }

        /**
         * Ingest siteVisitEvents for the respective customer.
         * Stored in hash map siteVisitMap with key as the starting week for event_time and
         * the value is list of siteVisits in that week for the particular customer.
         */
        if (e.getString("type").equalsIgnoreCase(siteVisit)) {
            String customerId = e.getString("customer_id");
            DateCalculator dc = new DateCalculator(e.getString("event_time"));
            String firstDay = dc.getFirstDay();
            SiteVisit s = new SiteVisit();
            s.setVerb(e.getString("verb"));
            s.setKey(e.getString("key"));
            s.setEventTime(e.getString("event_time"));
            s.setCustomerId(customerId);
            s.setTags(new ArrayList());
            if (siteVisits.containsKey(customerId)) {
                HashMap<String, ArrayList<SiteVisit>> siteVisitTemp = siteVisits.get(customerId);
                if (siteVisitTemp.containsKey(firstDay)) {
                    ArrayList<SiteVisit> siteVisitList = siteVisitTemp.get(firstDay);
                    siteVisitList.add(s);
                } else {
                    ArrayList<SiteVisit> siteVisitListNew = new ArrayList();
                    siteVisitListNew.add(s);
                    siteVisitTemp.put(firstDay, siteVisitListNew);
                }
            } else {
                ArrayList<SiteVisit> tempList = new ArrayList();
                tempList.add(s);
                siteVisitMap.put(firstDay, tempList);
                siteVisits.put(customerId, siteVisitMap);
            }
            calculateSimpleLTV(customerId);
        }
        /**
         * Ingest Order events for the customer.
         * Marks the old Order entry as inactive and set current entry as active entry if Order update is received.
         * Stored in hash map orderMap with key as the week start date for event_time and
         * the value is list of orders in that week for the particular customer.
         */
        if (e.getString("type").equalsIgnoreCase(order)) {
            String customerId = e.getString("customer_id");
            DateCalculator dc = new DateCalculator(e.getString("event_time"));
            String firstDay = dc.getFirstDay();
            Order o = new Order();
            o.setVerb(e.getString("verb"));
            o.setKey(e.getString("key"));
            o.setEventTime(e.getString("event_time"));
            o.setCustomerId(customerId);
            o.setTotalAmount(Float.parseFloat(e.getString("total_amount").replaceAll("USD", "").trim()));
            if (orders.containsKey(customerId)) {
                HashMap<String, ArrayList<Order>> orderTemp = orders.get(customerId);
                if (orderTemp.containsKey(firstDay)) {
                    ArrayList<Order> orderList = orderTemp.get(firstDay);
                    if (e.getString("verb").equalsIgnoreCase("UPDATE")) {
                        for (Order ord : orderList) {
                            if (ord.active == 1 && ord.getKey().equalsIgnoreCase(e.getString("key"))) {
                                ord.active = 0;
                            }
                        }
                    }
                    orderList.add(o);
                } else {
                    ArrayList<Order> orderListNew = new ArrayList();
                    orderListNew.add(o);
                    orderTemp.put(firstDay, orderListNew);
                }
            } else {
                ArrayList<Order> newOrderList = new ArrayList();
                newOrderList.add(o);
                orderMap.put(firstDay, newOrderList);
                orders.put(customerId, orderMap);
            }
            calculateSimpleLTV(customerId);
        }

        /**
         * Ingest image events for the customer.
         * All the image events are stored in hashMap with key as week start date for event_time.
         */
        if (e.getString("type").equalsIgnoreCase(image)) {
            String customerId = e.getString("customer_id");
            DateCalculator dc = new DateCalculator(e.getString("event_time"));
            String firstDay = dc.getFirstDay();
            Image i = new Image();
            i.setVerb(e.getString("verb"));
            i.setKey(e.getString("key"));
            i.setEventTime(e.getString("event_time"));
            i.setCustomerId(customerId);
            i.setCameraMake(e.getString("camera_make"));
            i.setCameraModel(e.getString("camera_model"));
            if (images.containsKey(customerId)) {
                HashMap<String, ArrayList<Image>> temp = images.get(customerId);
                if (temp.containsKey(firstDay)) {
                    ArrayList<Image> imageList = temp.get(firstDay);
                    imageList.add(i);
                } else {
                    ArrayList<Image> imageListNew = new ArrayList();
                    imageListNew.add(i);
                    temp.put(firstDay, imageListNew);
                }
            } else {
                ArrayList<Image> tempList = new ArrayList();
                tempList.add(i);
                imageMap.put(firstDay, tempList);
                images.put(customerId, imageMap);
            }
        }
    }


    public static void main(String[] args) {
        String e;
        String inputString;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fr = new FileReader(inputPath);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            inputString = sb.toString();
            JSONArray eventArray = new JSONArray(inputString);
            for (int i = 0; i < eventArray.length(); i++) {
                ingest(eventArray.getJSONObject(i), D);
            }
            fr.close();
            br.close();
            TopXSimpleLTVCustomers(10, D);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}