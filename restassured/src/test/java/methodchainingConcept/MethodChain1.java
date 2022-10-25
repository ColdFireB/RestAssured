package methodchainingConcept;

public class MethodChain1 {
	
	public static void main(String[] args) {
		a1().a2().a3();
	}
	
	public static MethodChain1 a1() {
		System.out.println("This is first method");
		return new MethodChain1();
	}
	
	public MethodChain1 a2() {
		System.out.println("This is second method");
		return this;
	}
	
	public MethodChain1 a3() {
		System.out.println("This is third method");
		return this;
	}

}
