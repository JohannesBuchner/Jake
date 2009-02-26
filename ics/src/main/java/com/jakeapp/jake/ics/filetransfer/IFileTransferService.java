package com.jakeapp.jake.ics.filetransfer;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.exceptions.CommunicationProblemException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * For transferring huge files
 * 
 * This is the main class that handles (aggregates) the transfer methods, it is
 * meant to handle fail-overs.
 * 
 * <h3>Information about the FileTransfer Service</h3>
 * <p>
 * NOTE: The FileTransfer Service doesn't actually handle files. It is more of a
 * content addressing method. So you ask for a content by a name the other can
 * identify (over the FileRequestFileMapper interface) and receive the content
 * for it. Choose a wise naming that can be transmitted over the used MsgService
 * (some don't like weird characters like &lt;, &gt;, &amp; etc.).
 * 
 * The decision whether a transfer is allowed is also handled there
 * (FileRequestFileMapper). You don't have to really use the files names, it
 * just has to be a addressing of content (e.g. you could append a version
 * number to the filename).
 * </p>
 * 
 * <p>
 * Note that the FileTransferService is, contrary to popular file exchange, not
 * a sending mechanism, but a request-content -&gt; receive-content mechanism.
 * You can not actively send someone content. You can only answer requests for
 * content and request content.
 * </p>
 * 
 * <p>
 * NOTE: The FileTransferService does not necessarily guarantee that a file that
 * is returned to be transferred "successfully" isn't messed up. You have to do
 * a check (e.g. using hashing) yourself.
 * 
 * Furthermore, expect problems when trying to send/request empty files. Since
 * you know the filesize before receiving, filter out these requests.
 * </p>
 * 
 * <p>
 * Temporary files are used for the transfer to ensure that the file stays the
 * same. For receiving, a file is created, content is saved there and after
 * successful transmission provided for you.
 * </p>
 * 
 * @author johannes
 */
public interface IFileTransferService {

	/**
	 * adds a TransferMethod. Add multiple in order to allow fallback in case of
	 * a {@link CommunicationProblemException} in request negotiations. The
	 * first added is tried first, the last added is tried last (FIFO).
	 * 
	 * @param m
	 * @param negotiationService
	 * @param user
	 * @throws NotLoggedInException
	 */
	void addTransferMethod(ITransferMethodFactory m, IMsgService negotiationService,
			UserId user) throws NotLoggedInException;

	/**
	 * requests a file and handles fail-over (this is a facade)
	 * 
	 * @param request
	 * @param nsl
	 * @throws NullPointerException
	 *             if no TransferMethods have been registered
	 */
	public void request(FileRequest request, INegotiationSuccessListener nsl);

	/**
	 * starts serving on all registered TransferMethods
	 * 
	 * @param l
	 * @param mapper
	 * @throws NotLoggedInException
	 */
	public void startServing(IncomingTransferListener l, FileRequestFileMapper mapper)
			throws NotLoggedInException;

	/**
	 * stops serving on all registered TransferMethods
	 */
	public void stopServing();

}
