package com.shadowfax.apps.securesms.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.StringTokenizer;

import com.shadowfax.apps.securesms.Main;
import com.shadowfax.apps.securesms.commonmodules.PreferenceOperations;

import android.widget.Toast;


public class RSA {
	private final static BigInteger one      = new BigInteger("1");
	//private final static SecureRandom random = new SecureRandom();
	private static final Random random = new Random();

	private BigInteger privateKey;
	private BigInteger publicKey;
	private BigInteger modulus;

	private int N;    //generate an N-bit (roughly) public and private key
	private int publicKeySize;
	public RSA(int public_key_size) {
		this.N=128;	  
		
		this.publicKeySize=public_key_size;

		BigInteger p = RSA.GeneratePrime(N/2, random);
		BigInteger q = RSA.GeneratePrime(N/2, random);
		BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

		modulus    = p.multiply(q);                                  
		publicKey  = RSA.GeneratePrime(publicKeySize, random);     // common value in practice = 2^16 + 1		
		privateKey = publicKey.modInverse(phi);
	}
	public RSA(int public_key_size,BigInteger public_key,BigInteger modulus) //This constructor is used when sender has to encrypt the message.
	{
		this.publicKeySize=public_key_size;
		this.publicKey=public_key;
		this.modulus=modulus;
		this.privateKey=null;
	}
	public RSA(int public_key_size,BigInteger public_key,BigInteger private_key, BigInteger modulus) //This constructor is used when receiver has to decrypt the message.
	{
		this.publicKeySize=public_key_size;
		this.publicKey=public_key;
		this.privateKey=private_key;
		this.modulus=modulus;
	}
	public int getPublicKeySize()
	{
		return this.publicKeySize;
	}
	public BigInteger getPublicKey()
	{
		return this.publicKey;
	}
	public BigInteger getPrivateKey() throws NullPointerException
	{
		return this.privateKey;

	}
	public BigInteger getModulus()
	{
		return this.modulus;
	}
	private BigInteger encrypt(BigInteger original_message) {
		return original_message.modPow(publicKey, modulus);
	}
	private BigInteger decrypt(BigInteger encrypted_message) {
		return encrypted_message.modPow(privateKey, modulus);
	}
	public String toString() {
		String s = "";
		s += "public  = " + publicKey  + "\n";
		s += "private = " + privateKey + "\n";
		s += "modulus = " + modulus;
		return s;
	}
	public String getEncryptedMessage(String original_message)
	{
		String encrypted_message="";
		int block_size=16; //Block cipher with each block containing 16 characters.
		
		//Create message by converting string to integer
		//Divide the message into blocks of size publicKeySize-characters

		int start;
		int end;
		
		int length=original_message.length();
		if(length==0)
		{
			return "Length Zero";
		}
		int num_of_blocks=((length)/block_size)+1;

		//So, when passing for decryption you should select the appropriate blocks and pass them.
		String block;
		byte[] bytes;
		BigInteger encrypt_input;
		BigInteger encrypt_output;
		start=0;
		encrypted_message="";
		if(num_of_blocks<=1)
		{
			end=length;
		}
		else
			end=block_size;
		for(int i=1;i<=num_of_blocks;i++)
		{

			block=original_message.substring(start,end);
			
			bytes=block.getBytes();
			encrypt_input=new BigInteger(bytes);
			encrypt_output=encrypt(encrypt_input);
			encrypted_message=encrypted_message+" "+encrypt_output.toString();

			if(i==(num_of_blocks-1))
			{
				start=end;
				end=length;
			}
			else
			{
				start=end;
				end=end+block_size;
			}
		}
		return encrypted_message;
	}
	public String getDecryptedMessage(String encrypted_message)
	{
		String decrypted_message="";	  

		BigInteger encrypt_output;
		BigInteger decrypt_output;
		BigInteger reverse;
		byte[] bytes_reverse;

		//Each block of data is separated by space. So, you can use StringTokenizer.
		StringTokenizer st=new StringTokenizer(encrypted_message);
		String block,temp;

		decrypted_message="";
		while(st.hasMoreTokens())
		{
			block=st.nextToken();
			encrypt_output=new BigInteger(block);
			decrypt_output=decrypt(encrypt_output);

			//extracting the message from the decrypted string 
			reverse=decrypt_output;
			bytes_reverse=reverse.toByteArray();

			temp=new String(bytes_reverse);
			decrypted_message=decrypted_message+temp;
		}
		return decrypted_message;
	}


	private static boolean miller_rabin_pass(BigInteger a, BigInteger n) {
		BigInteger n_minus_one = n.subtract(BigInteger.ONE);  
		BigInteger d = n_minus_one;
		int s = d.getLowestSetBit();
		d = d.shiftRight(s);
		BigInteger a_to_power = a.modPow(d, n);
		if (a_to_power.equals(BigInteger.ONE)) return true;
		for (int i = 0; i < s-1; i++) {
			if (a_to_power.equals(n_minus_one)) return true;
			a_to_power = a_to_power.multiply(a_to_power).mod(n);
		}
		if (a_to_power.equals(n_minus_one)) return true;
		return false;
	}
	public static boolean miller_rabin(BigInteger n) {
		for (int repeat = 0; repeat < 20; repeat++) {
			BigInteger a;
			do {
				a = new BigInteger(n.bitLength(), random);
			} while (a.equals(BigInteger.ZERO));
			if (!miller_rabin_pass(a, n)) {
				return false;
			}
		}
		return true;
	}
	public static BigInteger GeneratePrime(int nbits,Random rnd) {


		//  int nbits = Integer.parseInt("128");
		BigInteger p;
		do {
			p = new BigInteger(nbits, rnd);
			if (p.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) continue;
			if (p.mod(BigInteger.valueOf(3)).equals(BigInteger.ZERO)) continue;
			if (p.mod(BigInteger.valueOf(5)).equals(BigInteger.ZERO)) continue;
			if (p.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) continue;
		} while (!miller_rabin(p));
		// System.out.println(p);
		//System.out.println(miller_rabin(p) ? "PRIME" : "COMPOSITE");
		return p;
	}
}