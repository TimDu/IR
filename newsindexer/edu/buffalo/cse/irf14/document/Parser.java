/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS
		if (filename == null) {
			throw new ParserException();
		}
		File file = new File(filename);
		if (!file.exists() || !file.isFile()) {
			throw new ParserException();
		}
		File subDir = file.getParentFile();
		if (!subDir.exists() || !subDir.isDirectory()) {
			throw new ParserException();
		}
		// Document should be valid by here
		Document doc = new Document();
		FieldNames field = FieldNames.TITLE;
		List<String> authors = new LinkedList<String>();
		String content = new String();
		boolean startContent = false;

		doc.setField(FieldNames.FILEID, file.getName());
		doc.setField(FieldNames.CATEGORY, subDir.getName());
		
		try {
			BufferedReader reader =
					new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			
			while (line != null) {
				if (!line.isEmpty()) {
					switch(field) {
					case TITLE:
						doc.setField(field, line);
						field = FieldNames.AUTHOR;
						break;
					case AUTHOR:
					case AUTHORORG:
						if (line.startsWith("<AUTHOR>") ||
								!authors.isEmpty()) {
							String []seg = null;
							String []author = null;
							
							line = line.replace("<AUTHOR>", "");
							seg = line.split(",");
							author = seg[0].split("and");
							author[0] = author[0].
									replaceAll("BY|By|by", "");
							// Refines author names
							for (int i = 0; i < author.length; ++i) {
								author[i] = author[i].trim();
							}
							authors.addAll(Arrays.asList(author));
							
							if (line.endsWith("</AUTHOR>")) {
								// Saves author
								doc.setField(FieldNames.AUTHOR, authors.
										toArray(new String[authors.size()]));
								// Saves author organization
								if (seg.length > 1) {
									doc.setField(FieldNames.AUTHORORG,
											seg[1].replace("</AUTHOR>", "").
											trim());
								}
								field = FieldNames.PLACE;
							}
							break;
						} else {
							// This should be a PLACE
							field = FieldNames.PLACE;
						}
					case PLACE:
						String []seg = line.split("-");
						String []meta = seg[0].split(",");
						if (meta.length > 1) {
							doc.setField(FieldNames.PLACE, 
									seg[0].substring(0,
											seg[0].lastIndexOf(",")).trim());
							doc.setField(FieldNames.NEWSDATE,
									meta[meta.length - 1].trim());
						} else {
							meta[0] = meta[0].trim();
							try {
							new SimpleDateFormat(
									"MMM d", Locale.ENGLISH).parse(meta[0]);
							// This is a news date
							doc.setField(FieldNames.NEWSDATE, meta[0]);
							} catch (ParseException e) {
								// This is a place
								doc.setField(FieldNames.PLACE, meta[0]);
							}
						}
						field = FieldNames.CONTENT;
					case CONTENT:
						if (!startContent) {
							// First line of content
							String []cont = line.split("-");
							if (cont.length > 1) {
								for (int i = 1; i < cont.length; ++i) {
									content += cont[i];
								}
								startContent = true;
							}
						} else {
							content += line;
						}
					default: break;
					}
				}
				line = reader.readLine();
			}
			doc.setField(FieldNames.CONTENT, content);
			
			reader.close();
		} catch (IOException e) {
			throw new ParserException();
		}				

		return doc;
	}
}
