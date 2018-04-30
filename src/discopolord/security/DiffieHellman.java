package discopolord.security;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import misc.Log;

public class DiffieHellman {

	private BigInteger Alice;
	private BigInteger Bob;
	private BigInteger alicespublic;
	private BigInteger bobspublic;
	private BigInteger sharedkeyA;
	private BigInteger sharedkeyB;
	
	public BigInteger getPrivateA() {
		return Alice;
	}
	public BigInteger getPrivateB() {
		return Bob;
	}
	public BigInteger getPublicA() {
		return alicespublic;
	}
	public BigInteger getPublicB() {
		return bobspublic;
	}
	public BigInteger getSharedkeyA() {
		return sharedkeyA;
	}
	public BigInteger getSharedkeyB() {
		return sharedkeyB;
	}
	public static BigInteger getG() {
		return g;
	}
	private static BigInteger g;
	private boolean debug=false;	
	
	public DiffieHellman(boolean debug) {
		//	Empty, will accept arguments later when needed
	}
	public DiffieHellman() {
		
	}
	
	public BigInteger bigIntSqRootFloor(BigInteger x){
	    if (x.compareTo(BigInteger.ZERO) < 0) {
	        return BigInteger.ZERO;
	    }
	    if (x .equals(BigInteger.ZERO) || x.equals(BigInteger.ONE)) {
	        return x;
	    }
	    BigInteger two = BigInteger.valueOf(2L);
	    BigInteger y;
	    for (y = x.divide(two);
	            y.compareTo(x.divide(y)) > 0;
	            y = ((x.divide(y)).add(y)).divide(two));
	    return y;
	}
	
	public boolean is_prime(BigInteger number) {
		BigInteger Nsqrt = bigIntSqRootFloor(number);
		for (BigInteger i = BigInteger.valueOf(2); i.compareTo(Nsqrt) <= 0; i = i.add(BigInteger.ONE)) {
			if(number.mod(i) == BigInteger.ZERO) return false;
		}
		return true;
	}
	public boolean safetycheck(BigInteger n) {
		if(is_prime(n) && is_prime((n.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2)))))
			return true;
		return false;
	}
	
	public BigInteger getnthprime(int which) {
		int current = 1;
		BigInteger result = BigInteger.valueOf(2);
		if(debug) {
			Log.info("requested " + which + " prime");
			Log.info("c " + current + " w " + which);	
		}
		
		while (current != which) {
			if(is_prime(result)) {
				current++;
			}
			if(current == which) break;
			result = result.add(BigInteger.ONE);
		}
		return result;
	}
	
	public boolean is_prime_root(BigInteger n, BigInteger m) {
		Set <BigInteger> numberset = new HashSet<>();
		for (BigInteger i = BigInteger.ZERO; i.compareTo(m.multiply(BigInteger.valueOf(2))) < 0; i = i.add(BigInteger.ONE)) {
			numberset.add(n.modPow(i, m));
		}
		if(debug)
			Log.info("size " + numberset.size());
		if(BigInteger.valueOf(numberset.size()).equals(m.subtract(BigInteger.ONE)))
			return true;
		else return false;
	}
	
	public void generateKeys() {
		Random randGen = new Random();
		if(debug) {
			Log.info("Random " + randGen.nextInt(156000));
			Log.info("Number is prime?: " + is_prime(BigInteger.valueOf(24)));
		}
		
		g = BigInteger.ONE;
		BigInteger prime = getnthprime(randGen.nextInt(1300));
		if(debug) Log.info("Selected prime "+ prime);
		g = BigInteger.valueOf(randGen.nextInt(1300));
		while(true) {
			g = BigInteger.valueOf(randGen.nextInt(1300));
			if(g.compareTo(prime.subtract(BigInteger.ONE))<= 0 && is_prime_root(g, prime))break;
		}
		if(debug)
			Log.info("G number = " + g);

		Alice = BigInteger.valueOf(randGen.nextInt(1750));
		Bob = BigInteger.valueOf(randGen.nextInt(1750));
		if(debug)
			Log.info("Private keys :" + Alice + " " + Bob);		
		
		alicespublic = g.modPow(Alice, prime);
		bobspublic = g.modPow(Bob, prime);
		if(debug)
			Log.info("Public keys :" + alicespublic.toString() + " " + bobspublic.longValue());
		
		sharedkeyA = bobspublic.modPow(Alice, prime);
		sharedkeyB = alicespublic.modPow(Bob, prime);
		if(debug)
			Log.info("Shared keys :" + sharedkeyA.toString() + " " + sharedkeyB.toString());
		Log.success("Keys ready");
	}
}
