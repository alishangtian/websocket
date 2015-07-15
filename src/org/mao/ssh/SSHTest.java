package org.mao.ssh;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.junit.Test;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHTest {
	public static void main(String[] args) {
		try {
			// 读取服务器磁盘空间信息命令，读取使用率大于 90%的
			String cmd = "tail -f /opt/redhat/jboss-eap-5.1/jboss-as/server/bbcbg/log/server.log";
			JSch sshSingleton = new JSch();
			// 从配置文件中加载用户名和密码
			String userName = "gomebbc";
			String password = "gomebbc";
			String name = "server1";
			String server = "10.126.53.144";
			System.out.println("Start working on: " + name);
			Session session = sshSingleton.getSession(userName, server);
			session.setPassword(password);
			Properties config = new Properties();
			// 设置 SSH 连接时不进行公钥确认
			config.put("StrictHostKeyChecking", "no");
			// session.setConfig(config);
			session.connect();
			// 打开命令执行管道
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					channel.getInputStream()));
			channel.setCommand(cmd);
			channel.connect();
			// 读取命令输出信息
			String msg;
			while ((msg = in.readLine()) != null) {
				System.out.println(msg);
			}
			channel.disconnect();
			session.disconnect();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUtil() {
		try {
			Scanner scanner = new Scanner(System.in);// 创建输入流扫描器
			ShellUtils.execCmd(scanner.nextLine(), "bbcuser", "bbcuser",
					"10.126.53.144");
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}
    @Test
	public void test() {
		try {
			// 读取服务器磁盘空间信息命令，读取使用率大于 90%的
			String cmd = "df -h | grep -b -E \\(9[1-9]\\%\\)\\|\\(100\\%\\)";
			JSch sshSingleton = new JSch();
			// 从配置文件中加载用户名和密码
			Properties userProp = new Properties();
			userProp.load(new FileReader("conf/user.properties"));
			String userName = userProp.getProperty("username");
			String password = userProp.getProperty("password");
			// 从配置文件中加载服务器信息
			Properties serversProp = new Properties();
			serversProp.load(new FileReader("conf/servers.properties"));

			for (Map.Entry<Object, Object> serverProp : serversProp.entrySet()) {
				String name = (String) serverProp.getKey();
				String server = (String) serverProp.getValue();

				System.out.println("Start working on: " + name);
				Session session = sshSingleton.getSession(userName, server);
				session.setPassword(password);
				Properties config = new Properties();
				// 设置 SSH 连接时不进行公钥确认
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				// 打开命令执行管道
				ChannelExec channel = (ChannelExec) session.openChannel("exec");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						channel.getInputStream()));
				channel.setCommand(cmd);
				channel.connect();
				// 读取命令输出信息
				String msg;
				while ((msg = in.readLine()) != null) {
					System.out.println(msg);
				}
				channel.disconnect();
				session.disconnect();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}
}
