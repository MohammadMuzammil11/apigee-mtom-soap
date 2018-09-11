package com.google.apigee;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.Message;
import com.apigee.flow.message.MessageContext;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import java.io.ByteArrayOutputStream;


public class MtomHandler implements Execution {

	public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {
		
		try {

			MTOMEntityBuilder builder = MTOMEntityBuilder.create();
			builder.setMtomMultipart();
			builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);
			HttpEntity entity = builder.build();

			Message message = (Message) messageContext.getVariable("message");
			boolean destination = false;
			if (message == null) {
				destination = true;
				message = messageContext.createMessage(messageContext
						.getClientConnection()
						.getMessageFactory()
						.createRequest(messageContext));
			}
			//message.setContent(streamToString(entity.getContent()));
			message.setHeader("Content-Type", "multipart/form-data");
			//if(destination){
	//			messageContext.setVariable("request.body","coso");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.writeTo(baos);
				messageContext.setVariable("variable_1", baos.toString("UTF-8"));
				EntityUtils.consume(entity);
			//}
            return ExecutionResult.SUCCESS;

		} catch (Exception e) {
			e.printStackTrace();
			return ExecutionResult.SUCCESS;
		}
	}
}