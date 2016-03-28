package ssbse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	private static HashMap<String, FragmentPairList> fragHash = new HashMap<String, FragmentPairList>();
	private static Logger log;
	private static int agreedToolLimit = 2;
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

		String logFile = inputDir + ".txt";
		try {
			appender = new RollingFileAppender(layout, logFile, false);
			appender.setMaxFileSize("10MB");
			log.addAppender(appender);
		} catch (IOException e1) {
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
		List<File> listOfFiles = 
			(List<File>) FileUtils.listFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File file : listOfFiles) {
			// System.out.println("Processing " + file.getName());
			log.debug("Processing " + file.getName());
			try {
				GCF gcf = readGCF(file);
				int toolIndex = -1;
				if (file.getName().toLowerCase().trim().contains("ccfx"))
					toolIndex = Utilities.CCFX_INDEX;
				else if (file.getName().toLowerCase().trim().contains("simian"))
					toolIndex = Utilities.SIMIAN_INDEX;
				else if (file.getName().toLowerCase().trim().contains("nicad"))
					toolIndex = Utilities.NICAD_INDEX;
				else if (file.getName().toLowerCase().trim().contains("deckard"))
					toolIndex = Utilities.DECKARD_INDEX;
				processCloneFragment(gcf, toolIndex);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		calculateAgreedLOCs(calSumAgreedClonedLines(Utilities.NUMBER_OF_TOOLS));
		log.debug(printFragmentHash(agreedToolLimit));
	}
	
	// TODO: double-check for correctness
	/***
	 * Calculate fitness value of agreed LOCs
	 * @param arr array of agreed LOCs according to the number of tools
	 */
	private static void calculateAgreedLOCs(int[] arr) {
		long realLocs = 0;
		long idealLocs = 0;
		for (int i=0; i<arr.length; i++) {
			log.debug("Agreed " + (i+1) + " tools: " + arr[i] + " LOCs");
			realLocs += (i+1) * arr[i];
			idealLocs += Utilities.NUMBER_OF_TOOLS * arr[i];
		}
		double fitness = ((double) realLocs)/idealLocs;
		log.debug("Fitness: " + fitness);
		System.out.println(fitness);
	}
	
	/***
	 * Calculate agreed LOC
	 * @param gcf GCF document
	 */
	private static int[] calSumAgreedClonedLines(int numTools) {
		int[] countLoc = new int[numTools];
		Iterator it = fragHash.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, FragmentPairList> pair = (Map.Entry)it.next();
	        FragmentPairList fpList = (FragmentPairList) pair.getValue();
	        for (int i=0; i<fpList.size(); i++) {
	        	FragmentPair fp = fpList.get(i);
	        	int agreedLines = fp.getAgreedLines();
	        	int maxAgreedTools = fp.getAgreedTools();
	        	// fill in the agreed LOC according to #agreedTools
	        	for (int j=0; j<maxAgreedTools; j++) {
	        		countLoc[j] += agreedLines;
	        	}
	        }
	    }
	    return countLoc;
	}
	
	/***
	 * Add all the clone pairs to a hash map
	 * @param gcf GCF document
	 */
	private static void processCloneFragment(GCF gcf, int toolIndex) {
		int count=0;
		for (CloneClass cloneClass : gcf.getCloneClasses()) {
			
			String pair = "";
			String out = "";
			// only pair of clones
			if (cloneClass.getClones().size() == 2) {
				Fragment frag1 = cloneClass.getClones().get(0).getFragmentList().get(0);
				Fragment frag2 = cloneClass.getClones().get(1).getFragmentList().get(0);
				out += frag1.getFile() + ":" + frag1.getStartLine() + ":" + frag1.getEndLine() + "|";
				out += frag2.getFile() + ":" + frag2.getStartLine() + ":" + frag2.getEndLine();
				pair += frag1.getFile() + ":" + frag2.getFile();

				// log.debug(pair);
				// add to the fragment hash
				addToFragmentHash(pair, frag1, frag2, toolIndex);
//				System.out.println("add to hash (2) = (" + frag1.getStartLine() + "," 
//				+ frag1.getEndLine() + ") : (" + frag2.getStartLine() + "," + frag2.getEndLine() + ")");
//				count++;
//				System.out.println(printFragmentHash());
			} else if (cloneClass.getClones().size() > 2) {
				// log.debug("clone id = " + cloneClass);
				// log.debug("clone size = " + cloneClass.getClones().size());
				// more than a pair
				for (int i=0; i < cloneClass.getClones().size(); i++) {
					for (int j=i; j < cloneClass.getClones().size(); j++) {
						Clone clone1 = cloneClass.getClones().get(i);
						Clone clone2 = cloneClass.getClones().get(j);
						pair = "";
						out = "";
						Fragment frag1 = clone1.getFragmentList().get(0);
						Fragment frag2 = clone2.getFragmentList().get(0);
						// check if not the same pair, just reverse
						if (frag1.getFile().equals(frag2.getFile()) 
								&& frag1.getStartLine() == frag2.getStartLine()
								&& frag1.getEndLine() == frag2.getEndLine()) {
							continue;
						}
						else {
							// TODO: this assumes that each clone has only 1 fragment.
							// Should be fixed in the future to be safer.
							out += frag1.getFile() + ":" + frag1.getStartLine() + ":" + frag1.getEndLine() + "|";
							out += frag2.getFile() + ":" + frag2.getStartLine() + ":" + frag2.getEndLine();
							pair += frag1.getFile() + ":" + frag2.getFile();
						}
						// log.debug(out);
						// add to the fragment hash
						addToFragmentHash(pair, frag1, frag2, toolIndex);
//						System.out.println("add to hash (>2) = (" + frag1.getStartLine() + "," 
//						+ frag1.getEndLine() + ") : (" + frag2.getStartLine() + "," + frag2.getEndLine() + ")");
//						count++;
//						System.out.println(printFragmentHash());
					}
				}
			} else {
				log.error("No clone pair found");
			}
		}
	}
	
	/***
	 * Add the two given fragments to the fragment list if they do not exist. 
	 * If they do, update the agreed lines
	 * @param pair the key composed of two file names
	 * @param frag1 the 1st fragment
	 * @param frag2 the 2nd fragment
	 */
	public static void addToFragmentHash(String pair, Fragment frag1, Fragment frag2, int toolIndex) {
		if (fragHash.get(pair) == null) {
			// System.out.println("adding a fresh new one");
			FragmentPairList list = new FragmentPairList();
			list.addFragmentPair(new FragmentPair(frag1, frag2, toolIndex));
			fragHash.put(pair, list);
		} else {
			// System.out.println("Oh! already exists");
			FragmentPairList list = fragHash.get(pair);
			// if found in range, update the value of agreed lines
			if (list.isInListAndUpdate(new FragmentPair(frag1, frag2, toolIndex))) {
				// System.out.println("It's in the range. So I updated it.");
				return;
			} else {
				// if not found, just add them.
				// System.out.println("But not this range, just add it.");
				list.addFragmentPair(new FragmentPair(frag1, frag2, toolIndex));
			}
		}
	}
	
	/***
	 * Print out the hash map of fragments
	 * @param agreedToolLimit number of minimum agreed tools to print
	 * @return String of all fragments which have been reported by at least >= agreedToolLimit
	 */
	public static String printFragmentHash(int agreedToolLimit) {
		StringBuffer sb = new StringBuffer();
		sb.append("=============== Clone Fragments ==============\n");
		Iterator it = fragHash.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        // sb.append("key: " + pair.getKey() + "\n");
	        FragmentPairList fpList = (FragmentPairList) pair.getValue();
	        // sb.append("value:");
	        sb.append(fpList.toString(agreedToolLimit));
	        // System.out.println(pair.getKey() + " = " + pair.getValue());
	    }
	    sb.append("=============== Clone Fragments ==============\n");
	    
	    return sb.toString();
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

		// log.debug("Root element :"
		//		+ doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("CloneClass");
		// log.debug("Number of clone classes = " + nList.getLength());
		// loop through all clone classes (skip the first child which is the ID)
		for (int i = 0; i < nList.getLength(); i++) {
			CloneClass cc = new CloneClass();
			Node n = nList.item(i);
			NodeList clones = n.getChildNodes();
//			log.debug(i + ": Node name = " + n.getNodeName() + ", child nodes = " + clones.getLength());
			// loop through all clones
			for (int j = 0; j < clones.getLength(); j++) {
				Clone c = new Clone();
				Node clone = clones.item(j);
				
				// Add only the real clones, skip ID
				if (!clone.getNodeName().toString().equals("#text")
						&& !clone.getNodeName().toString().equals("ID")) {
					NodeList fragments = clone.getChildNodes();
//					log.debug("> " + j + ": Node name = " + clone.getNodeName() + ", childs = " + fragments.getLength());
					// loop through all fragments
					for (int k = 0; k < fragments.getLength(); k++) {
						Fragment frag = new Fragment();
						Node fNode = fragments.item(k);
						if (!fNode.getNodeName().toString().equals("#text")) {
//							log.debug(
//									">> Node name = " + fNode.getNodeName());
							NodeList fChildNodes = fNode.getChildNodes();
							int vindex = 0;
							for (int l = 0; l < fChildNodes.getLength(); l++) {
								Node fragNode = fChildNodes.item(l);
								if (!fragNode.getNodeName().toString()
										.equals("#text")) {
//									log.debug("      >> Node name = "
//											+ fragNode.getNodeName() + ", value = "
//													+ fragNode.getTextContent());

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
		formater.printHelp("java -jar target/calAgreedLOC.jar -i <input dir>", options);
		System.exit(0);
	}
}
