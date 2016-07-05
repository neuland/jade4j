package de.neuland.jade4j;

import java.io.FileNotFoundException;


public class TestFileHelper {

    public static final String TESTFILE_LEXER_FOLDER = "/lexer/";
    public static final String TESTFILE_LEXER_0_0_8_FOLDER = "/lexer_0.0.8/";
    public static final String TESTFILE_PARSER_FOLDER = "/parser/";
    public static final String TESTFILE_COMPILER_FOLDER = "/compiler/";
    public static final String TESTFILE_ORIGINAL_FOLDER = "/originalTests/";
    public static final String TESTFILE_20150927_ORIGINAL_FOLDER = "/originalTests20150927/";
    public static final String TESTFILE_20150515_ORIGINAL_FOLDER = "/originalTests20150515/";
    public static final String TESTFILE_ISSUES_FOLDER = "/issues/";
    public static final String TESTFILE_COMPILER_ERROR_FOLDER = "/compiler/errors/";
    
	public static String getResourcePath(String fileName) throws FileNotFoundException {
		try {
			return TestFileHelper.class.getResource(fileName).getFile();
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public static String getRootResourcePath() throws FileNotFoundException {
		return getResourcePath("/");
	}

	public static String getLexerResourcePath(String fileName) throws FileNotFoundException {
		return getResourcePath(TESTFILE_LEXER_FOLDER + fileName);
	}

    public static String getLexer_0_0_8_ResourcePath(String fileName) throws FileNotFoundException {
        return getResourcePath(TESTFILE_LEXER_0_0_8_FOLDER + fileName);
    }
    
	public static String getParserResourcePath(String fileName) {
		try {
			return getResourcePath(TESTFILE_PARSER_FOLDER + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
    
	public static String getCompilerResourcePath(String fileName) {
		try {
			return getResourcePath(TESTFILE_COMPILER_FOLDER + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getOriginalResourcePath(String fileName) {
		try {
			return getResourcePath(TESTFILE_ORIGINAL_FOLDER + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getOriginal20150927ResourcePath(String fileName) {
		try {
			return getResourcePath(TESTFILE_20150927_ORIGINAL_FOLDER + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getOriginal20150515ResourcePath(String fileName) {
		try {
			return getResourcePath(TESTFILE_20150515_ORIGINAL_FOLDER + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getIssuesResourcePath(String fileName) {
		try {
			return getResourcePath(TESTFILE_ISSUES_FOLDER + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCompilerErrorsResourcePath(String fileName) {
	    try {
	        return getResourcePath(TESTFILE_COMPILER_ERROR_FOLDER + fileName);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
}
