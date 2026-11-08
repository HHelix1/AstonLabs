public class Main {
    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();

        map.put("Value1", 10);
        map.put("Value2", 20);
        map.put("Value3", 30);
        
        map.put("Value4", 15);

        System.out.println("Value1: " + map.get("Value1"));     
        System.out.println("Value2: " + map.get("Value2"));   
        System.out.println("Value3: " + map.get("Value3"));     

        System.out.println("Removed Value2: " + map.remove("Value2")); 
        System.out.println("Value2 after remove: " + map.get("Value2")); 
        System.out.println("Total size: " + map.size()); 
    }
}