package org.eclipse.cdt.internal.ui.compare;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.parser.IParser;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.ISourceElementRequestor;
import org.eclipse.cdt.core.parser.ParserFactory;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.internal.core.parser.ScannerInfo;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.compare.structuremergeviewer.IStructureCreator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
/**
 * 
 */
public class CStructureCreator implements IStructureCreator {

	private static final String NAME = "CStructureCreator.name";

	public CStructureCreator() {
	}

	/**
	 * @see IStructureCreator#getTitle
	 */
	public String getName() {
		return CUIPlugin.getResourceString(NAME);
	}

	/**
	 * @see IStructureCreator#getStructure
	 */
	public IStructureComparator getStructure(Object input) {

		String s = null;
		if (input instanceof IStreamContentAccessor) {
			try {
				s = readString(((IStreamContentAccessor) input).getContents());
			} catch (CoreException ex) {
			}
		}

		if (s == null) {
			s = new String();
		}
		Document doc = new Document(s);

		CNode root = new CNode(null, ICElement.C_UNIT, "root", doc, 0, 0);

		ISourceElementRequestor builder = new CParseTreeBuilder(root, doc);
		try {
			IScanner scanner =
				ParserFactory.createScanner(new StringReader(s), "code", new ScannerInfo(), ParserMode.QUICK_PARSE, builder);
			IParser parser = ParserFactory.createParser(scanner, builder, ParserMode.QUICK_PARSE);
			parser.parse();
		} catch (Exception e) {
			// What to do when error ?
			// The CParseTreeBuilder will throw CParseTreeBuilder.ParseError
			// for acceptProblem.
		}

		return root;
	}

	/**
	 * @see IStructureCreator#canSave
	 */
	public boolean canSave() {
		return true;
	}

	/**
	 * @see IStructureCreator#locate
	 */
	public IStructureComparator locate(Object path, Object source) {
		return null;
	}

	/**
	 * @see IStructureCreator#canRewriteTree
	 */
	public boolean canRewriteTree() {
		return false;
	}

	/**
	 * @see IStructureCreator#rewriteTree
	 */
	public void rewriteTree(Differencer differencer, IDiffContainer root) {
	}

	/**
	 * @see IStructureCreator#save
	 */
	public void save(IStructureComparator structure, Object input) {
		if (input instanceof IEditableContent && structure instanceof CNode) {
			IDocument doc = ((CNode) structure).getDocument();
			IEditableContent bca = (IEditableContent) input;
			String c = doc.get();
			bca.setContent(c.getBytes());
		}
	}

	/**
	 * @see IStructureCreator#getContents
	 */
	public String getContents(Object node, boolean ignoreWhitespace) {
		if (node instanceof IStreamContentAccessor) {
			IStreamContentAccessor sca = (IStreamContentAccessor) node;
			try {
				return readString(sca.getContents());
			} catch (CoreException ex) {
			}
		}
		return null;
	}

	/**
	 * Returns null if an error occurred.
	 */
	private static String readString(InputStream is) {
		if (is == null)
			return null;
		BufferedReader reader = null;
		try {
			StringBuffer buffer = new StringBuffer();
			char[] part = new char[2048];
			int read = 0;
			reader = new BufferedReader(new InputStreamReader(is));

			while ((read = reader.read(part)) != -1)
				buffer.append(part, 0, read);

			return buffer.toString();

		} catch (IOException ex) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
				}
			}
		}
		return null;
	}

}
