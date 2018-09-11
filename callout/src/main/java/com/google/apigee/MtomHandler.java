package com.google.apigee;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.Message;
import com.apigee.flow.message.MessageContext;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;


public class MtomHandler implements Execution {

	public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {

		try {
			String contentType = messageContext.getVariable("contentType");
			String binaryData = messageContext.getVariable("binaryData");
			String soapRequest = messageContext.getVariable("flow.soap.request");
			String fileName = messageContext.getVariable("fileName");
			String soapUUID = UUID.randomUUID().toString()+"@apigee.com";
			String partUUID = UUID.randomUUID().toString()+"@apigee.com";
			soapRequest = soapRequest.replace("{fileName}", fileName).replace("{uuid}", partUUID);
			if(contentType == null || "".equals(contentType)){
				contentType = "application/octet-stream";
			}

			MTOMEntityBuilder builder = MTOMEntityBuilder.create();
			builder.setMtomMultipart();
			ContentType xopType = ContentType.create("application/xop+xml").withCharset("utf-8")
					.withParameters(new BasicNameValuePair("type", "text/xml"));
			builder.setContentType(xopType);
			builder.addTextBody(soapUUID, soapRequest , xopType);
			if("text/plain".equals(contentType)){
				String decoded = binaryData;
				if(decoded == null){
					 decoded = "";
				}
				builder.addTextBody(partUUID, decoded, ContentType.TEXT_PLAIN.withCharset("utf-8"));
			}else if("application/octet-stream".equals(contentType)){
				byte [] decoded;
				if(binaryData == null){
					decoded = new byte[0];
				}else {
					decoded = Base64.decodeBase64(binaryData);
				}
				builder.addBinaryBody(partUUID, decoded);
			}
			HttpEntity entity = builder.build();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.writeTo(baos);
			Message message = (Message) messageContext.getVariable("message");
			message.setHeader("Content-Type", "multipart/related; boundary="+builder.getBoundary()+";\n" +
					" type=\"application/xop+xml\"; start=\"<"+soapUUID+">\";" +
					" start-info=\"text/xml; charset=utf-8\"");
			message.setContent(new ByteArrayInputStream(baos.toByteArray()));
			try {
				EntityUtils.consume(entity);
				baos.close();
			}catch (Exception ex){}//IGNORED
            return ExecutionResult.SUCCESS;

		} catch (Exception e) {
			messageContext.setVariable("exp", e.getMessage());
			return ExecutionResult.SUCCESS;
		}
	}
}