package cn.zern.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * FTP工具类
 * <p>获取client</p>
 * <p>上传文件</p>
 * <p>下载文件</p>
 * <p>删除文件</p>
 * <p></p>
 * @author zern
 *
 * 2016年8月15日下午3:00:49
 */
public class FTPUtils {
	
	private String _username;
	private String _password;
	private String _url;
	private Integer _port;
	private String _enconding;
	private FTPClient client;
	
	private Logger log = Logger.getLogger(getClass());
	
	
	public FTPUtils(String _username, String _password, String _url) {
		this(_username, _password, _url, 21);
	}

	public FTPUtils(String _username, String _password, String _url,
			Integer _port) {
		this(_username, _password, _url,_port,"UTF-8");
	}

	public FTPUtils(String _username, String _password, String _url,
			Integer _port, String _enconding) {
		super();
		this._username = _username;
		this._password = _password;
		this._url = _url;
		this._port = _port;
		this._enconding = _enconding;
	}
	
	/**
	 * 配置文件地址
	 * @param configPath
	 */
	public FTPUtils(String configPath){
		PropertiesUtils pro = new PropertiesUtils(configPath);
		String username = pro.getProperty("ftp.user");
		String password = pro.getProperty("ftp.password");
		String url = pro.getProperty("ftp.url");
		Integer port = pro.getInteger("ftp.port", 21);
		String encoding = pro.getProperty("ftp.encoding", "UTF-8");
		this._username = username;
		this._password = password;
		this._url = url;
		this._port = port;
		this._enconding = encoding;
	}
	
	/**
	 * 构建FTpClient
	 */
	private void buildClient(){
		if (client == null || !client.isConnected()) {
			FTPClient client = null;
			try {
				client = new FTPClient();
				client.connect(_url, _port);
				client.login(_username, _password);
				if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
					log.info("未连接到FTP，用户名或密码错误。");
					client.disconnect();
				} else {
					log.info("FTP连接成功。");
				}
				client.setControlEncoding(_enconding);
				client.setBufferSize(1024);
				client.setConnectTimeout(6000);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.client = client;
		}
	}
	
	/**
	 * 上传文件至服务器
	 * @param srcFile	源文件
	 * @param remotePath	服务器路径
	 * @return
	 */
	public Boolean uploadFile(File srcFile,String remotePath){
		return uploadFile(srcFile, srcFile.getName(), remotePath);
	}
	
	/**
	 * 上传文件至服务器
	 * @param srcFile	源文件
	 * @param newFileName	上传后文件名
	 * @param remotePath	服务器路径
	 * @return
	 */
	public Boolean uploadFile(File srcFile, String newFileName, String remotePath){
		buildClient();
		InputStream is;
		Boolean success = false;
		try {
			is = new FileInputStream(srcFile);
			client.changeWorkingDirectory(remotePath);
			success = client.storeFile(newFileName, is);
			is.close();
			client.disconnect();
		} catch (FileNotFoundException e) {
			log.info("Srcfile not found");
			e.printStackTrace();
		} catch (IOException e) {
			log.info("上传失败");
			e.printStackTrace();
		}
		
		return success;
	}

	public FTPClient getClient() {
		buildClient();
		return client;
	}
	
	
	
	

}
