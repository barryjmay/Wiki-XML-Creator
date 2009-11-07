/*
    Wiki XML Creator
    Copyright (c) 2009 Kim Hauritz <kim.hauritz@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wikiXmlCreator;

import java.io.*;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) throws java.io.IOException,
			java.io.FileNotFoundException {

		InputStream inputFile = null;

		String DocumentType = "xml";

		int SkipLinesTop = 0;
		int SkipLinesBottom = 0;
		String ChapterRegX = "";
		boolean dropLinks = false;
		boolean noMainPg = false;
		int splitBy = PdfToHtmlXMLConverter.SPLIT_BY_PAGE;
		int splitAfterNumPages = 1;
		String DocumentTitle = "";
		boolean TestRegularExpression = false;

		// Check to see if we're forcing "single user mode" or not
		if (args.length > 0) {
			String tempArg = "";
			for (int i = 0; i < args.length; i++) {
				tempArg = args[i];
				if (tempArg.equals("-h") || tempArg.equals("-?")) {

					System.out
							.println("Usage: wikixmlcreator [OPTIONS]... [FILE] (- for stdin)\n\n"
									+ "Converts a html/text/xml into WikiMedia article XML format\n\n"
									+ "ATTENTION! Currently only xml output from pdftohtml is supported!\n"
									+ "\n\nArguments:\n"
									+ "     -t TYPE       \t xml (pdftohtml)/ html / txt \n"
									+ "                   \t Choose the type of the inputfile (xml Works!)\n\n"
									+ "     -split OPTION \t Create articles by p (page) og c (chapter) \n"
									+ "     -pages NUM    \t Number of pages per chapter \n"
									+ "     -title \"TITLE\"  \t Title of the book \n"
									+ "     -nolinks      \t Remove links from the file (ONLY XML FOR NOW)\n"
									+ "     -nomainpg     \t Disable main page containing list of pages/chapter\n"
									+ "     -skiptop NUM  \t Skip NUM lines from top of page (xml only)\n"
									+ "     -skipbtm NUM  \t Skip NUM lines from bottom of page (xml only)\n"
									+ "     -chapter \"REGX\" \t Regular expression describing a chapter\n"
									+ "     -testregx     \t Test the -chapter Regular expression\n"
									+ "     -h -?         \t Help this page.\n\n");

					System.exit(0);
				}

				if (tempArg.equals("-t") && moreArgs(args, i)) {
					DocumentType = args[i + 1];
				}

				if (tempArg.equals("-split") && moreArgs(args, i)) {

					if (args[i + 1].equals("c"))
						splitBy = PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER;
				}

				if (tempArg.equals("-pages") && moreArgs(args, i)) {
					try {
						splitAfterNumPages = Integer.parseInt(args[i + 1]);
					} catch (Exception e) {
						System.out.println("Error with argument -pages : "
								+ args[i + 1] + " is not a valid integer");
					}
					
				}

				if (tempArg.equals("-title") && moreArgs(args, i)) {
					DocumentTitle = args[i + 1];
				}

				if (tempArg.equals("-nolinks")) {
					dropLinks = true;
				}
				
				if (tempArg.equals("-nomainpg")) {
					noMainPg = true;
				}

				if (tempArg.equals("-skiptop") && moreArgs(args, i)) {
					try {
						SkipLinesTop = Integer.parseInt(args[i + 1]);
					} catch (Exception e) {
						System.out.println("Error with argument -skiptop : "
								+ args[i + 1] + " is not a valid integer");
					}
				}

				if (tempArg.equals("-skipbtm") && moreArgs(args, i)) {
					try {
						SkipLinesBottom = Integer.parseInt(args[i + 1]);
					} catch (Exception e) {
						System.out.println("Error with argument -skipbtm : "
								+ args[i + 1] + " is not a valid integer");
					}
				}

				if (tempArg.equals("-chapter") && moreArgs(args, i)) {

					ChapterRegX = args[i + 1];
				}

				if (tempArg.equals("-testregx")) {

					TestRegularExpression = true;
				}

			}

			// //////////////////////////////
			if (tempArg != null) {
				// tempArg should be the last argument in our list
				if (tempArg.equals("-")) {
					// If the last argument is "-" read from stdin
					inputFile = System.in;
				} else {
					File f = new File(tempArg);

					if (f.exists())
						inputFile = new FileInputStream(tempArg);
					else
						System.out
								.println("File: '" + tempArg + "' Not found.");
				}

			} else
				System.out.println("Error no input file supplied");
			
			if (DocumentType.toLowerCase().equals("xml") && inputFile != null) {
				PdfToHtmlXMLConverter pthconv = new PdfToHtmlXMLConverter();
				pthconv.load(inputFile);

				pthconv.setSplitBy(splitBy);
				pthconv.setSplitAfterNumPages(splitAfterNumPages);
				pthconv.setDocumentTitle(DocumentTitle);
				pthconv.setDropLinks(dropLinks);
				pthconv.setSkipLinesTop(SkipLinesTop);
				pthconv.setSkipLinesBottom(SkipLinesBottom);
				pthconv.setChapterRegX(ChapterRegX);
				pthconv.setNoMainPage(noMainPg);

				if (TestRegularExpression)
					pthconv.testRegX(true);
				else
				{
					pthconv.convert();
					pthconv.PrintAllPages();
				}

			}

		}
		/*
		 * InputStream is;
		 * 
		 * if (args.length == 1) is = new FileInputStream(args[0]); else is =
		 * System.in;
		 * 
		 * if (is != null) { try {
		 * //System.out.println("starting PdfToHtmlXMLConverter");
		 * el
		 * 
		 * pthconv.convert(); //pthconv.testRegX();
		 * 
		 * pthconv.PrintAllPages(); } catch (Exception e) {
		 * System.out.println("Main exception"); e.printStackTrace(); } }
		 */

	}

	/**
	 * Check to see if there are more arguments remaining in the given list
	 * 
	 * @param args
	 *            The list of arguments
	 * @param pos
	 *            The position in the list to start checking from
	 * @return <code>true</code> There is an additional argument available
	 *         <code>false</code> There are no more arguments available
	 */
	private static boolean moreArgs(final String[] args, final int pos) {
		boolean retVal = false;

		if (pos + 1 < args.length && args[pos + 1] != null) {
			retVal = true;
		}

		return retVal;

	}

}
