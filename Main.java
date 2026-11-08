public class Main {
    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();

        map.put("Value 1", 10);
        map.put("Value 2", 20);
        map.put("Value 3", 30);
        
        map.put("Value 4", 15);

        System.out.println("Value 1: " + map.get("Value 1"));     // 15
        System.out.println("Value 2: " + map.get("Value 2"));   // 20
        System.out.println("Value 3: " + map.get("Value 3"));     // null

        System.out.println("Removed Value 2: " + map.remove("Value 2")); // 20
        System.out.println("Value 2 after remove: " + map.get("Value 2")); // null
        System.out.println("Total size: " + map.size()); // 2
    }
}