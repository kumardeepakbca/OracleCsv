package com.parse;


public class Constants {
 
	public static final String query="SELECT CAST (ls.filoanid as integer) as \"LOAN_NUM\", LA.WORKFLOWSTATECODE workflow,LA.CREATEDATE RFP_dt,LA.APPLICATIONRECTIVEDDATE app_dt,"
					+" CAST(LPS.MARKETTYPE as integer) as \"MARKETTYPE\","
					+" AL02.CPIKEY, LPS.BASERATEDATE. LP ACTIONTAKEN,"
					+" LP.ACTIONDATE, LA.LOCKDATE,LA.LOANAMOUNT,LPS.LOANTERM,LP.LTV,LP.CLTV,LP.ACLTV, LA.FICOSCORE,"
					+" LPS.RATE, LPS.POINTS, LPS.LOCKPERIODDAYS,	LPS.LOCKSTATUS,LP.AGENCY,LPS.PROGRAMDESC, ENMA.DESCRIPTION Prop_type,"
					+" rt.description \"OCCUPANCY\",AD.STATECODE,"
					+" (SELECT LT.DESCR1PTION from T_loantype LT where LT.LOANTYPECODE=LPS.LOANTYPECODE) LOANTYPE,"
					+" (SELECT LPT.DESCR1PTION from T_loanpurposetype LPT where LPT.LOANPURPOSECODE=LA.LOANPURPOSECODE) LOANPURPOSECODE,"
					+" (CASE WHEN LPS.AMORTIZATIONTYPECODE='01' THEN 'ARM' WHEN LPS.AMORTIZATIONTYPECODE='05' THEN 'Fixed' END) \"AMORTIZATIONTYPE\","
					+" (CASE WHEN alo.chennel IS NULL THEN 'RETAIL' WHEN alo.chennel = 'HLD' THEN 'CD' END) \"Ctanne1\" ,"
					+" ALO.HLDCUSTOMERTYPE, (SELECT oe4.NAME from T_orgentity oe4 where oe4.orgentityid=alo.origbranchid ) BRANCH,"
					+" par.pricevalue,LP.BRANCHDISCREITION,lps.DOCTYPE from T_LOANAPPLICATION la, "
					+" T_ADDITIONALLOANINFO alo,"
					+" T_ADDITIONALLOANINFO2 alo2,"
					+" T_LOANPRODUCTSUMMARY lps,"
					+" T_TRANSACTION tr,"
					+" T_PROPERTY pt,"
					+" T_RESIDENCETYPE rt,"
					+" T_ADDR ad,"
					+" T_FNMAPROPERTYTYPE fnma,"
					+" T_LOANPRODUCT lp left outer join T_PRODUCTAPPLIEDRULE par on par.loanproductid=lp.loanproductid and par.pricingtypecode= 'LMT' where la.filoanid not like '199%' and ALO.PLANTYPE='loan' "
					+" and LA.APPLICATIONRECEIVEDATE >= To_Date('2016/01/01 12:00:00 am','yyyy/mm/dd hh:mi:ss am')"
					+" and LPS.BASERATEDATE between TO_DATE('2017/05/24 12:00:00 am','yyyy/mm/dd hh:mi:ss am') and TO_DATE('2017/05/25 12:00:00 am','yyyy/mm/dd hh:mi:ss am')"
					+" and ( (LPS.MARKETTYPE in ('229') ) or  (LPS.MARKETTYPE in ('110') ) and LPS.LOANTYPECODE ='03') "
					+" and PT.RESISDENCETYPECODE=RT.RESISDENCETYPECODE "
					+" and LA.TRANSACTIONID=TR.TRANSACTIONID "
					+" and TR.PROPERTYID=PT.PROPERTYID "
					+" and PT.ADDRID=AD.ADDRID "
					+" and PT.SUBJECTPROPERTYTYPECODE=FNMA.SUBJECTPROPERTYTYPECODE "
					+" and LA.LOANAPPLICATIONID=LPS.LOANAPPLICATIONID "
					+" and ALO.LOANINFOID=LA.LOANINFOID "
					+" and ALO2.LOANINFO2ID=LA.LOANINFO2ID "
					+" and ALO2.LOANINFO2ID=LA.LOANINFO2ID "
					+" and LA.LOANAPPLICATIONID=LP.LOANAPPLICATIONID "
					//+" and lp.loanproductid=( SELECT MAX(lp2.loanproductid) FROM t_loanproduct lp2 where lp2.loanapplicationid=la.loanapplicationid "
					+" and lp.loanproductid=( SELECT MAX(lp2.loanproductid) FROM t_loanproduct lp2 where lp2.loanapplicationid=la.loanapplicationid and lp2.ACTIONTAKEN in('LOCKED','PRLOCKED') "
					+" order by LA.FILOANID,LP.ACTIONDATAE";  
		

	}
