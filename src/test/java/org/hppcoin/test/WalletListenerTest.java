/*
 * HPPCOIN License
 * 
 * Copyright (c) 2017-2018, HPPCOIN Developers.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.hppcoin.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hppcoin.crons.WalletListener;
import org.junit.Test;

import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Transaction;

public class WalletListenerTest {
//	@Test
	public void getnewAddress() {
		WalletListener wallet = new WalletListener(false);
		String newaddress = wallet.getNewAddress();
		assertEquals('h', newaddress.charAt(0));
		assertEquals(34, newaddress.length());
		System.out.println(wallet.dumpPrivKey(newaddress));
	}

//	@Test
	public void listtransactions() {
		List<Integer> list=new ArrayList<>();
		list.add(5);
		list.add(1);
		list.add(25);
		Collections.sort(list);
		System.out.println(list.get(0));
		WalletListener wallet = new WalletListener(false);
		List<Transaction> transactions = wallet.listTransactions();
		for (Transaction tx : transactions)
		{		System.out.println("account:" + tx.account() + " amount:" + tx.amount() + " address:" + tx.address()
					+ " txid:" + tx.txId() + " time:" + tx.time().getTime() + " confirmation:" + tx.confirmations());
		System.out.println(tx.toString());
		}
	}
//	@Test
	public void listtLMNodesTest() {
		
		WalletListener wallet = new WalletListener(false);
		
		System.out.println(wallet.listLMNodes());
		assertEquals(0, 0);
		
	}
	
//	@Test
	public void winnerLMNodesTest() {
		
		WalletListener wallet = new WalletListener(false);
		
		System.out.println(wallet.current());
		assertEquals(0, 0);
		
	}

}
