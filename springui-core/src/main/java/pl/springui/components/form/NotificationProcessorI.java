package pl.springui.components.form;

import java.util.List;

public interface NotificationProcessorI {
	String processMessages(List<Message> messages);
}
