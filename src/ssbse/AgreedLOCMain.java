package ssbse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/***
 * A class to read several GCF XML files and produces agreed LOC 
 * 
 * @author Chaiyong Ragkhitwetsagul, UCL
 * @version 0.1
 * @since 2016-03-20
 */
public class AgreedLOCMain {
	/***
	 * An array list to store list of all files and their line counts.
	 */
	private static HashMap<String, FragmentList> cloneFragmentHash = new HashMap<String, FragmentList>();
	private static Logger log;
	// create the Options
	private static Options options = new Options();
	private static String inputDir = "";

	public static void main(String[] args) {
		// process the command line arguments
		processCommandLine(args);
		
		// BasicConfigurator.configure();
		// PropertyConfigurator.configure(logProperties);
		log = Logger.getLogger(AgreedLOCMain.class);
		// setting up a FileAppender dynamically...
		SimpleLayout layout = new SimpleLayout();
		RollingFileAppender appender;

		String logFile = inputDir + ".log";
		try {
			appender = new RollingFileAppender(layout, logFile, false);
			// appender.setMaxFileSize("100MB");
			log.addAppender(appender);
		} catch (IOException e1) {
			// e1.printStackTrace();
			// If cannot use the log file output, show it on screen.
			BasicConfigurator.configure();
		}

		// set logging level
		log.setLevel(Level.DEBUG);
		log.debug("Running AgreedLOC v. 0.1");
		
		// get all GCF files in the directory
		log.debug("inputDir = " + inputDir);
		File folder = new File(inputDir);
		@SuppressWarnings("unchecked")
		List<File> listOfFiles = (List<File>) FileUtils.listFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File file : listOfFiles) {
			log.debug("Processing " + file.getName());
			try {
				GCF gcf = readGCF(file);
				calSumAgreedClonedLines(gcf);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * Calculate agreed LOC
	 * @param GCF document
	 */
	private static int calSumAgreedClonedLines(GCF gcf) {
		int sum = 0;
		for (CloneClass cloneClass : gcf.getCloneClasses()) {
			String pair = "";
			for (Clone clone: cloneClass.getClones()) {
				for (Fragment frag: clone.getFragmentList()) {
					pair += frag.getFile() + ":";
				}
			}
			log.debug(pair);
		}
		return sum;
	}

	/***
	 * Read the GCF file in XML format
	 * 
	 * @param the GCF file (.xml). Some part of codes 
	 *  copied from http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private static GCF readGCF(File f) throws ParserConfigurationException,
			SAXException, IOException {
		// Create a GCF document 
		GCF gcf = new GCF();
		// Read the given GCF file in XML format
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(f);
		doc.getDocumentElement().normalize();

		log.debug("Root element :"
				+ doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("CloneClass");
		log.debug("Number of clone classes = " + nList.getLength());
		// loop through all clone classes (skip the first child which is the ID)
		for (int i = 0; i < nList.getLength(); i++) {
			CloneClass cc = new CloneClass();
			Node n = nList.item(i);
			NodeList clones = n.getChildNodes();
			log.debug(i + ": Node name = " + n.getNodeName() + ", child nodes = " + clones.getLength());
			// loop through all clones
			for (int j = 0; j < clones.getLength(); j++) {
				Clone c = new Clone();
				Node clone = clones.item(j);

				// Add only the real clones, skip ID
				if (!clone.getNodeName().toString().equals("#text")
						&& !clone.getNodeName().toString().equals("ID")) {
					NodeList fragments = clone.getChildNodes();
					log.debug("> " + j + ": Node name = " + clone.getNodeName() + ", childs = " + fragments.getLength());
					// loop through all fragments
					for (int k = 0; k < fragments.getLength(); k++) {
						Fragment frag = new Fragment();
						Node fNode = fragments.item(k);
						if (!fNode.getNodeName().toString().equals("#text")) {
							log.debug(
									">> Node name = " + fNode.getNodeName());
							NodeList fChildNodes = fNode.getChildNodes();
							int vindex = 0;
							for (int l = 0; l < fChildNodes.getLength(); l++) {
								Node fragNode = fChildNodes.item(l);
								if (!fragNode.getNodeName().toString()
										.equals("#text")) {
									log.debug("      >> Node name = "
											+ fragNode.getNodeName() + ", value = "
													+ fragNode.getTextContent());

									if (vindex == 0) // file
										frag.setFile(fragNode.getTextContent());
									else if (vindex == 1) // start
										frag.setStartLine(Integer
												.parseInt(fragNode
														.getTextContent()));
									else
										// end
										frag.setEndLine(Integer
												.parseInt(fragNode
														.getTextContent()));
									vindex++;
								}
							}

							// add fragment to the clone
							c.addFragment(frag);
						}
					}
					// add clone to the clone class
					cc.addClone(c);
				}
			}
			// add clone class to the GCF document
			gcf.addCloneClass(cc);
		}
		log.debug("Number of clone classes (after parsing) = " + gcf.getCloneClassSize());
		
		return gcf;
	}

	/***
	 * A helper to open a given file
	 * 
	 * @param filePath - location of the file
	 * @return a File object of the given file
	 */
	private static File fileReader(String filePath) {
		File f = new File(filePath);
		return f;
	}
	
	private static void processCommandLine(String[] args) {
		// create the command line parser
		CommandLineParser parser = new BasicParser();

		options.addOption("i", "input", true, "Directory containing all GCF files");
		options.addOption("h", "help", false, "Display help message.");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("i")) {
				inputDir = line.getOptionValue("i");
			} else { throw new ParseException("No GCF directory provided."); }
			
			if (line.hasOption("h")) {
				showHelp();
			}
		} catch (ParseException exp) {
			showHelp();
			log.error("Warning: " + exp.getMessage());
		}
	}
	
	private static void showHelp() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("AgreedLOC", options);
		System.exit(0);
	}
}
