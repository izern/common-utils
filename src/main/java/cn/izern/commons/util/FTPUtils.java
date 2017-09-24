package cn.izern.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 构造函数,获取连接
	 * @param _username
	 * @param _password
	 * @param _url
	 */
	public FTPUtils(String _username, String _password, String _url) {
		this(_username, _password, _url, 21);
	}

	/**
	 * 构造函数,获取连接
	 * @param _username
	 * @param _password
	 * @param _url
	 * @param _port
	 */
	public FTPUtils(String _username, String _password, String _url,
			Integer _port) {
		this(_username, _password, _url,_port,"UTF-8");
	}

	/**
	 * 构造函数,获取连接
	 * @param _username
	 * @param _password
	 * @param _url
	 * @param _port
	 * @param _enconding
	 */
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
	@Deprecated
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
			changeDiretory(remotePath);
			success = client.storeFile(newFileName, is);
			is.close();
			log.info("upload file "+newFileName+" success");
			close();
		} catch (FileNotFoundException e) {
			log.info("Srcfile not found");
			e.printStackTrace();
		} catch (IOException e) {
			log.info("上传失败");
			e.printStackTrace();
		}
		
		return success;
	}
	
	/**
	 * 批量上传文件
	 * @param files	源文件
	 * @param fileNames	上传后文件名
	 * @param remotePath 服务器路径
	 * @return
	 */
	public Boolean uploadFiles(List<File> files, List<String> fileNames,
			String remotePath){
		if (fileNames.size() != files.size()) {
			throw new IndexOutOfBoundsException("files size must be equal to fileNames size");
		}
		buildClient();
		Boolean success = false;
		try {
			changeDiretory(remotePath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int i = 0; i < files.size(); i++){
			try {
				InputStream is;
				is = new FileInputStream(files.get(i));
				success = client.storeFile(fileNames.get(i), is);
				is.close();
				if (!success) {
					break;
				}
				log.info("upload file "+fileNames.get(i)+" success");
			} catch (FileNotFoundException e) {
				log.info("Srcfile not found");
				e.printStackTrace();
			} catch (IOException e) {
				log.info("上传失败");
				e.printStackTrace();
			}
		}
		close();
		return success;
	}
	
	/**
	 * 批量上传文件，文件名不变
	 * @param files	源文件
	 * @param remotePath 服务器路径
	 * @return
	 */
	public Boolean uploadFiles(List<File>files, String remotePath){
		List<String> fileNames = new ArrayList<String>();
		for (File file : files) {
			fileNames.add(file.getName());
		}
		return uploadFiles(files, fileNames, remotePath);
	}
	
	/**
	 * 下载文件
	 * @param fileName	服务器文件名
	 * @param remotePath 服务器路径
	 * @param LocalFile	下载至本地文件
	 * @return
	 */
	public Boolean downloadFile(String fileName,String remotePath,File LocalFile){
		buildClient();
		Boolean success = false;
		try {
			changeDiretory(remotePath);
			FTPFile [] files = client.listFiles();
			for (FTPFile ftpFile : files) {
				if (ftpFile.getName().equals(fileName)) {
					OutputStream os = new FileOutputStream(LocalFile);
					client.retrieveFile(fileName, os);
					os.flush();
					os.close();
					success = true;
				}
			}
			if (success) {
				log.info("download file success");
			}else {
				log.info("could not find file");
			}
		} catch (IOException e) {
			log.info("下载失败");
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * 关闭ftp连接
	 */
	public void close(){
		if (client != null && client.isConnected()) {
			try {
				client.logout();
				client.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 改变当前路径，多级目录使用/分隔,如果目录不存在则新建目录
	 * @param path
	 * @throws IOException 
	 */
	public void changeDiretory(String path) throws IOException{
		buildClient();
		client.changeWorkingDirectory("/");
		String[] paths = path.split("/");
		for (String string : paths) {
			if (StringUtils.isBlank(string)) {
				continue;
			}
			if (!client.changeWorkingDirectory(string)) {
				client.makeDirectory(string);
				client.changeWorkingDirectory(string);
			}
		}
	}
	
	public FTPClient getClient() {
		buildClient();
		return client;
	}

}
