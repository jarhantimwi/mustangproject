
/**
 * *********************************************************************
 * <p>
 * Copyright 2019 Jochen Staerk
 * <p>
 * Use is subject to license terms.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p>
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * **********************************************************************
 */
package org.mustangproject.ZUGFeRD;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.mustangproject.*;
import org.mustangproject.CII.CIIToUBL;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UBLTest extends ResourceCase {
	final String TARGET_XML = "./target/testout-1Lieferschein.xml";

	public void testUBLBasic() {

		// the writing part
		final CIIToUBL c2u = new CIIToUBL();
		final String sourceFilename = "factur-x.xml";
		final File input = getResourceAsFile(sourceFilename);
		final File expectedFile = getResourceAsFile("ubl-conv-ubl-output-factur-x.xml");
		String expected = null;
		String result = null;
		try {
			final File tempFile = File.createTempFile("ZUGFeRD-UBL-", "-test");
			c2u.convert(input, tempFile);
			expected = ResourceUtilities.readFile(StandardCharsets.UTF_8, expectedFile.getAbsolutePath()).replaceAll("\r\n", "\n");
			result = ResourceUtilities.readFile(StandardCharsets.UTF_8, tempFile.getAbsolutePath()).replaceAll("\r\n", "\n");
		} catch (final IOException e) {
			fail("Exception should not happen: " + e.getMessage());
		}


		assertNotNull(result);
		assertEquals(expected, result);
	}

	public void test1Lieferschein() {

		EinLieferscheinExporter oe = new EinLieferscheinExporter();
		Invoice i = new Invoice().setDueDate(new Date()).setIssueDate(new Date()).setDeliveryDate(new Date())
				.setSender(new TradeParty("Test company", "teststr", "55232", "teststadt", "DE").addTaxID("DE4711").addVATID("DE0815").setContact(new Contact("Hans Test", "+49123456789", "test@example.org")).addBankDetails(new BankDetails("DE12500105170648489890", "COBADEFXXX")))
				.setRecipient(new TradeParty("Franz Müller", "teststr.12", "55232", "Entenhausen", "DE"))
				.setReferenceNumber("991-01484-64")//leitweg-id
				.setNumber("123").addItem(new Item(new Product("Testprodukt", "", "C62", BigDecimal.ZERO), /*price*/ new BigDecimal("1.0"),  /*qty*/ new BigDecimal("1.0")).addReferencedLineID("A12"));


		try {
			oe.setTransaction(i);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			oe.export(baos);

			String theXML = baos.toString("UTF-8");
			assertTrue(theXML.contains("<DespatchAdvice"));
			Files.write(Paths.get(TARGET_XML), theXML.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
