
public class Pair<K, V> {
	private K key;
	private V value;
	
	public Pair(K k, V v){
		this.value = v;
		this.key = k;
	}
	
	public K getKey(){
		return this.key;
	}
	
	public V getValue(){
		return this.value;
	}
	
}
