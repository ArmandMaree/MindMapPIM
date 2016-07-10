package poller;

import java.util.*;
import com.google.api.services.gmail.model.*;

public class GmailBatchMessages {
	public List<Message> messages = null;
	public String nextPageToken = null;
}
