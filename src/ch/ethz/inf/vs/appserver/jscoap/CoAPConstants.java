package ch.ethz.inf.vs.appserver.jscoap;

import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Accept;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Bad_Gateway;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Bad_Option;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Bad_Request;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Changed;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Content;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Content_Type;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Created;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Deleted;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.ETag;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Forbidden;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Gateway_Timeout;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.If_Match;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Internal_Server_Error;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Location_Path;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Location_Query;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Max_Age;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Method_Not_Allowed;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Not_Found;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Not_Implemented;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Precondition_Failed;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Proxy_Uri;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Proxying_Not_Supported;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Request_Entity_Too_Large;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Service_Unavailable;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Token;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Unauthorized;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Unsupported_Media_Type;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Uri_Host;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Uri_Path;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Uri_Port;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Uri_Query;
import static ch.ethz.inf.vs.appserver.jscoap.CoAPConstants.Valid;
import coap.CodeRegistry;

/**
 * CoAPConstants implements the most important constants from CoAP.
 * 
 * @author Martin Lanter
 */
public interface CoAPConstants {

	// Option Number Registry from draft-ietf-core-coap-07.htm (chapter 11.2)
	public static final String Content_Type 	= "Content-Type";
	public static final String Max_Age 			= "Max-Age";
	public static final String Proxy_Uri 		= "Proxy-Uri";
	public static final String ETag 			= "ETag";
	public static final String Uri_Host 		= "Uri-Host";
	public static final String Location_Path 	= "Location-Path";
	public static final String Uri_Port 		= "Uri-Port";
	public static final String Location_Query 	= "Location-Query";
	public static final String Uri_Path 		= "Uri-Path";
	public static final String Token 			= "Token";
	public static final String Accept 			= "Accept";
	public static final String If_Match 		= "If-Match";
	public static final String Uri_Query 		= "Uri-Query";
	public static final String If_None_Match 	= "If-None-Match";
	
	// CodeRegistry constants as JS constants
	public static final int Created 			= CodeRegistry.RESP_CREATED;
	public static final int Deleted 			= CodeRegistry.RESP_DELETED;
	public static final int Valid 				= CodeRegistry.RESP_VALID;
	public static final int Changed 			= CodeRegistry.RESP_CHANGED;
	public static final int Content 			= CodeRegistry.RESP_CONTENT;
	public static final int Bad_Request 		= CodeRegistry.RESP_BAD_REQUEST;
	public static final int Unauthorized 		= CodeRegistry.RESP_UNAUTHORIZED;
	public static final int Bad_Option 			= CodeRegistry.RESP_BAD_OPTION;
	public static final int Forbidden 			= CodeRegistry.RESP_FORBIDDEN;
	public static final int Not_Found 			= CodeRegistry.RESP_NOT_FOUND;
	public static final int Method_Not_Allowed	= CodeRegistry.RESP_METHOD_NOT_ALLOWED;
	public static final int Precondition_Failed = CodeRegistry.RESP_PRECONDITION_FAILED;
	public static final int Request_Entity_Too_Large = CodeRegistry.RESP_REQUEST_ENTITY_TOO_LARGE;
	public static final int Unsupported_Media_Type = CodeRegistry.RESP_UNSUPPORTED_MEDIA_TYPE;
	public static final int Internal_Server_Error = CodeRegistry.RESP_INTERNAL_SERVER_ERROR;
	public static final int Not_Implemented 	= CodeRegistry.RESP_NOT_IMPLEMENTED;
	public static final int Bad_Gateway 		= CodeRegistry.RESP_BAD_GATEWAY;
	public static final int Service_Unavailable = CodeRegistry.RESP_SERVICE_UNAVAILABLE;
	public static final int Gateway_Timeout 	= CodeRegistry.RESP_GATEWAY_TIMEOUT;
	public static final int Proxying_Not_Supported = CodeRegistry.RESP_PROXYING_NOT_SUPPORTED;
}

class CoAPConstantsConverter {
	
	public static int convertHeaderToInt(String c) {
		if (Content_Type.equals(c)) 	return 1;
		else if (Max_Age.equals(c)) 	return 2;
		else if (Proxy_Uri.equals(c)) 	return 3;
		else if (ETag.equals(c)) 		return 4;
		else if (Uri_Host.equals(c))	return 5;
		else if (Location_Path.equals(c))return 6;
		else if (Uri_Port.equals(c))	return 7;
		else if (Location_Query.equals(c))return 8;
		else if (Uri_Path.equals(c))	return 9;
		else if (Token.equals(c))		return 11;
		else if (Accept.equals(c))		return 12;
		else if (If_Match.equals(c))	return 13;
		else if (Uri_Query.equals(c))	return 15;
		else return 21;
	}
	
	public static String convertIntToHeader(int nr) {
		if (nr==1) return Content_Type;
		else if (nr==2) return Max_Age;
		else if (nr==3) return Proxy_Uri;
		else if (nr==4) return ETag;
		else if (nr==5) return Uri_Host;
		else if (nr==6) return Location_Path;
		else if (nr==7) return Uri_Port;
		else if (nr==8) return Location_Query;
		else if (nr==9) return Uri_Path;
		else if (nr==11) return Token;
		else if (nr==12) return Accept;
		else if (nr==13) return If_Match;
		else if (nr==15) return Uri_Query;
		else return "Unknown";
	}
	
	public static int convertCoAPCodeToHttp(int code) {
		if (code==Created) return 201;
		else if (code==Deleted) return 204;
		else if (code==Valid) return 304;
		else if (code==Changed) return 204;
		else if (code==Content) return 200;
		else if (code==Bad_Request) return 400;
		else if (code==Unauthorized) return 401;
		else if (code==Bad_Option) return 400;
		else if (code==Forbidden) return 403;
		else if (code==Not_Found) return 404;
		else if (code==Method_Not_Allowed) return 405;
		else if (code==Precondition_Failed) return 412;
		else if (code==Request_Entity_Too_Large) return 413;
		else if (code==Unsupported_Media_Type) return 415;
		else if (code==Internal_Server_Error) return 500;
		else if (code==Not_Implemented) return 501;
		else if (code==Bad_Gateway) return 502;
		else if (code==Service_Unavailable) return 503;
		else if (code==Gateway_Timeout) return 504;
		else if (code==Proxying_Not_Supported) return 505;
		else return 0;
	}
	
	public static String convertCoAPCodeToString(int code) {
		return CodeRegistry.toString(code);
	}
}
