package eu.europa.osha.barometer.edition.database.model;

import java.util.ArrayList;
import java.util.HashMap;

public class QualitativeMSDataDAO {
	
	private static QualitativeMSDataDAO INSTANCE = null;
    private static final String url = "eu.europa.osha.barometer.edition.webui.conf.database_statements";

    private QualitativeMSDataDAO () { super(); }

    public static synchronized QualitativeMSDataDAO getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new QualitativeMSDataDAO();
        }
        return INSTANCE;
    }
    
    public ArrayList<HashMap<String,String>> getCountriesByMatrixPageSection(String page){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getCountriesByMatrixPageSection";
    		String[] queryParams = {page};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchSelect(query, queryParams, url);
    		if (result.size() > 0)
            {
                list = (ArrayList<HashMap<String, String>>) result.clone();
            }

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public ArrayList<HashMap<String,String>> getCountriesByStrategiesPageSection(String page){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getCountriesByStrategiesPageSection";
    		String[] queryParams = {page};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchSelect(query, queryParams, url);
    		if (result.size() > 0)
            {
                list = (ArrayList<HashMap<String, String>>) result.clone();
            }

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public ArrayList<HashMap<String,String>> getMatrixPageDataByCountryAndInstitution(String page, String country, String institution){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		int check1 = 0;
    		int check2 = 0;
    		int check3 = 0;
    		int check4 = 0;
    		
    		if(page.equals("MATRIX_AUTHORITY")) {
    			if(institution.equals("osh_authority")) {
    				check1 = 1;
    	    		check2 = 0;
    	    		check3 = 0;
    	    		check4 = 0;
    			} else if(institution.equals("compensation_insurance")) {
    				check1 = 0;
    	    		check2 = 1;
    	    		check3 = 0;
    	    		check4 = 0;
    			} else if(institution.equals("prevention_institute")) {
    				check1 = 0;
    	    		check2 = 0;
    	    		check3 = 1;
    	    		check4 = 0;
    			} else if(institution.equals("standardisation_body")) {
    				check1 = 0;
    	    		check2 = 0;
    	    		check3 = 0;
    	    		check4 = 1;
    			}
    		} else if(page.equals("MATRIX_STRATEGY")) {
    			if(institution.equals("implementation_record")) {
    				check1 = 1;
    	    		check2 = 0;
    	    		check3 = 0;
    	    		check4 = 0;
    			} else if(institution.equals("prevention_diseases")) {
    				check1 = 0;
    	    		check2 = 1;
    	    		check3 = 0;
    	    		check4 = 0;
    			} else if(institution.equals("tackling_demographic")) {
    				check1 = 0;
    	    		check2 = 0;
    	    		check3 = 1;
    	    		check4 = 0;
    			}
    		} else if(page.equals("MATRIX_STATISTICS")) {
    			if(institution.equals("osh_statistics")) {
    				check1 = 1;
    	    		check2 = 0;
    	    		check3 = 0;
    	    		check4 = 0;
    			} else if(institution.equals("surveys")) {
    				check1 = 0;
    	    		check2 = 1;
    	    		check3 = 0;
    	    		check4 = 0;
    			} else if(institution.equals("research_institutes")) {
    				check1 = 0;
    	    		check2 = 0;
    	    		check3 = 1;
    	    		check4 = 0;
    			}
    		}
    		
    		StringBuilder query =  new StringBuilder();
    		
    		query.append("(SELECT mp.id as data_id, mp.page as page, n.country_code as country_code, ");
    		query.append("t.id as translation_id, t.text as published_text, t.draft_text as updated_text ");
    		query.append("FROM matrix_page mp, nuts n, translation t ");
    		query.append("WHERE mp.page='");
    		query.append(page);
    		query.append("' AND mp.nuts_id=n.id AND n.country_code = '");
    		query.append(country);
    		query.append("' AND mp.check_1=");
    		query.append(check1);
    		query.append(" AND mp.check_2=");
    		query.append(check2);
    		query.append(" AND mp.check_3=");
    		query.append(check3);
    		query.append(" AND mp.check_4=");
    		query.append(check4);
    		query.append(" AND mp.text_1_literal_id = t.literal_id AND t.language='EN'");
    		query.append(")UNION(");
    		query.append("SELECT mp.id data_id, mp.page as page, n.country_code as country_code, ");
    		query.append("t2.id as translation_id, t2.text as published_text, t2.draft_text as updated_text ");
    		query.append("FROM matrix_page mp, nuts n, translation t2 ");
    		query.append("WHERE mp.page='");
    		query.append(page);
    		query.append("' AND mp.nuts_id=n.id AND n.country_code = '");
    		query.append(country);
    		query.append("' AND mp.check_1=");
    		query.append(check1);
    		query.append(" AND mp.check_2=");
    		query.append(check2);
    		query.append(" AND mp.check_3=");
    		query.append(check3);
    		query.append(" AND mp.check_4=");
    		query.append(check4);
    		query.append(" AND mp.text_2_literal_id = t2.literal_id AND t2.language='EN') ");
    		if(page.equals("MATRIX_AUTHORITY")) {
    			query.append("UNION(");
        		query.append("SELECT mp.id data_id, mp.page as page, n.country_code as country_code, ");
        		query.append("t3.id as translation_id, t3.text as published_text, t3.draft_text as updated_text ");
        		query.append("FROM matrix_page mp, nuts n, translation t3 ");
        		query.append("WHERE mp.page='");
        		query.append(page);
        		query.append("' AND mp.nuts_id=n.id AND n.country_code = '");
        		query.append(country);
        		query.append("' AND mp.check_1=");
        		query.append(check1);
        		query.append(" AND mp.check_2=");
        		query.append(check2);
        		query.append(" AND mp.check_3=");
        		query.append(check3);
        		query.append(" AND mp.check_4=");
        		query.append(check4);
        		query.append(" AND mp.text_3_literal_id = t3.literal_id AND t3.language='EN' )");
    		}
    		
    		String[] queryParams = {};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchPreparedQuery(query.toString());
    		if (result.size() > 0)
            {
                list = (ArrayList<HashMap<String, String>>) result.clone();
            }

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public ArrayList<HashMap<String,String>> getStrategiesPageDataByCountryAndInstitution(String page, String country){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		StringBuilder query =  new StringBuilder();
    		
    		query.append("(");
    		for(int i=1;i<=4;i++) {
    			query.append("SELECT sp.id as data_id, sp.page as page, n.country_code as country_code, t.text as published_text, ");
    			query.append("t.draft_text as updated_text, t.id as translation_id ");
        		query.append("FROM strategies_page sp, nuts n, translation t ");
        		query.append("WHERE sp.page='");
        		query.append(page);
        		query.append("' AND sp.nuts_id=n.id AND n.country_code = '");
        		query.append(country);
        		query.append("' AND sp.text_"+i+"_literal_id = t.literal_id AND t.language='EN'");
        		if(i != 4) {
        			query.append(")UNION(");
        		} else {
        			query.append(")");
        		}
    		}
    		
    		if(page.equals("STRATEGY")) {
    			for(int i=5;i<=8;i++) {
    				query.append("UNION(");
            		query.append("SELECT sp.id as data_id, sp.page as page, n.country_code as country_code, t.text as published_text, ");
            		query.append("t.draft_text as updated_text, t.id as translation_id ");
            		query.append("FROM strategies_page sp, nuts n, translation t ");
            		query.append("WHERE sp.page='");
            		query.append("page");
            		query.append("' AND sp.nuts_id=n.id AND n.country_code = '");
            		query.append(country);
            		query.append("' AND sp.text_"+i+"_literal_id = t.literal_id AND t.language='EN'");
            		query.append(")");
    			}
    		}
    		
    		String[] queryParams = {};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchPreparedQuery(query.toString());
    		if (result.size() > 0)
            {
                list = (ArrayList<HashMap<String, String>>) result.clone();
            }

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public boolean updateDraftText(String updated_text, String translation_id) {
    	boolean updatedCorrectly = false;
    	try {
	    	String query = "database.update.updateDraftText";
	    	String[] queryParams = {updated_text, translation_id};
	        JDBCDataSourceOperations.launchQuery(query, queryParams, url);
	        updatedCorrectly = true;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
            updatedCorrectly = false;
    	}
    	return updatedCorrectly;
    }
    
    public boolean undoDraftText(String translation_id) {
    	boolean updatedCorrectly = false;
    	try {
	    	String query = "database.update.undoDraftText";
	    	String[] queryParams = {translation_id};
	        JDBCDataSourceOperations.launchQuery(query, queryParams, url);
	        updatedCorrectly = true;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
            updatedCorrectly = false;
    	}
    	return updatedCorrectly;
    }
    
    public boolean publishLiteral(String translation_id, String updated_text) {
    	boolean updatedCorrectly = false;
    	try {
	    	String query = "database.update.publishLiteral";
	    	String[] queryParams = {updated_text, translation_id};
	        JDBCDataSourceOperations.launchQuery(query, queryParams, url);
	        updatedCorrectly = true;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
            updatedCorrectly = false;
    	}
    	return updatedCorrectly;
    }
}
