package org.pipeData.codeGen;


import org.pipeData.core.utils.RandomType;
import org.pipeData.core.utils.StringUtil;

/**
 * signKey生成器
 *
 * @author Chill
 */
public class SignKeyGenerator {

	public static void main(String[] args) {
		System.out.println("=======================================================");
		System.out.println("====== blade.token.sign-key 的值从中挑选一个便可 =========");
		System.out.println("=======================================================");
		for (int i = 0; i < 10; i++) {
			String signKey = StringUtil.random(32, RandomType.ALL);
			System.out.println("BladeX SignKey：[" + signKey + "] ");
		}
		System.out.println("=======================================================");
	}

}
