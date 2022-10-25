package methodchainingConcept;

public class OriginalWithoutMethodChain {
	public static void main(String[] args) {
		OriginalWithoutMethodChain obj = new OriginalWithoutMethodChain();
		obj.a1();
		obj.a2();
		obj.a3();
	}
	
	public void a1() {
		System.out.println("This is first method");
	}

	public void a2() {
		System.out.println("This is second method");
	}
	
	public void a3() {
		System.out.println("This is third method");
	}
}
