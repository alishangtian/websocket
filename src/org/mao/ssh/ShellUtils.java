package org.mao.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ShellUtils {
	private static JSch jsch;
	private static Session session;

	/**
	 * 连接到指定的IP
	 * 
	 * @throws JSchException
	 */
	public static void connect(String user, String passwd, String host)
			throws JSchException {
		jsch = new JSch();
		jsch.setKnownHosts("");
		session = jsch.getSession(user, host, 22);
		session.setPassword(passwd);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("userauth.gssapi-with-mic", "no");
		session.setConfig(config);
		session.connect();
	}

	/**
	 * 执行相关的命令
	 * 
	 * @throws JSchException
	 */
	public static void execCmd(String command, String user, String passwd,
			String host) throws JSchException {
		if (null == session || !session.isConnected()) {
			connect(user, passwd, host);
		}
		BufferedReader reader = null;
		Channel channel = null;
		try {
			System.out.println(session.toString());
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			channel.connect();
			InputStream in = channel.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			String buf = null;
			while ((buf = reader.readLine()) != null) {
				System.out.println(buf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			if (command.equals("close")) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				channel.disconnect();
				session.disconnect();
				System.out.println("session closed");
			}
		}
	}

	public static void main(String[] args) {
		try {
			while (true) {
				Scanner scanner = new Scanner(System.in);
				ShellUtils.execCmd(scanner.nextLine(), "bbcuser", "bbcuser",
						"10.126.53.144");
			}
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}
	
}
