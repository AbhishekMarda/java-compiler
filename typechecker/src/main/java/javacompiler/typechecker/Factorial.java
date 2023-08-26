class Mains {
	public static void main(String[] args) {
		System.out.println(new A().run(3));
	} 
}

class A {
	int a2; 
	
	public int run(int a3) {
		int a4;

		a4 = a3;
		a3 = a4;
		a3 = a2;
		a2 = a3;

		return 3;
	}
}