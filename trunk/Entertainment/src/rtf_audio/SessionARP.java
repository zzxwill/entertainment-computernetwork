package rtf_audio;

public class SessionARP {
	public String addr = null;
	public int port;
	SessionARP(String rtpSession) throws IllegalArgumentException {
		int offset;
		String portStr = null;
		if (rtpSession != null && rtpSession.length() > 0) {
			while (rtpSession.length() > 1 && rtpSession.charAt(0) == '/')
				rtpSession = rtpSession.substring(1);
			offset = rtpSession.indexOf('/');
			if (offset == -1) {
				if (!rtpSession.equals(""))
					addr = rtpSession;
			} else {
				addr = rtpSession.substring(0, offset);
				rtpSession = rtpSession.substring(offset + 1);
				offset = rtpSession.indexOf('/');
				if (offset == -1) {
					if (!rtpSession.equals("")){
						portStr = rtpSession;
					}
				}
			}
		}
		if (addr == null) {
			throw new IllegalArgumentException();
		}
		if (portStr != null) {
			try {
				Integer integer = Integer.valueOf(portStr);
				if (integer != null)
					port = integer.intValue();
			} catch (Throwable t) {
				throw new IllegalArgumentException();
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}
