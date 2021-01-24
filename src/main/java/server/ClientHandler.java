package server;



import FileData.FileReadWrite;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;
    private String login;

    private static final Logger logger = Logger.getLogger(ClientHandler.class);


    public ClientHandler(Server server, Socket socket) {
        try {

            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            server.getServerExService().execute(() -> {
                try {
                    try{
                        socket.setSoTimeout(120000);
                        // цикл аутентификации
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/")) {
                                if(str.startsWith("/reg ")){
                                    String[] token = str.split("\\s",4);
                                    boolean b = server.getAuthService().registration(token[1], token[2], token[3]);
                                    if(b){
                                        logger.info("Client is registered");
                                        sendMessage("/regOk");
                                    } else {
                                        logger.info("registration error");
                                        sendMessage("/regNo");
                                    }
                                }
                                if (str.startsWith("/auth ")) {
                                    String[] token = str.split("\\s",3);
                                    String newNick = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);
                                    if (newNick != null) {
                                        login = token[1];
                                        if(!server.isLoginAuthenticated(login)){
                                            nickname = newNick;
                                            out.writeUTF("/authOk " + nickname);
                                            server.subscribe(this);
                                            socket.setSoTimeout(0);
                                            logger.info("Client " + nickname + " is logged in");
                                            break;
                                        } else{
                                            out.writeUTF("Учетная запись уже используется");
                                        }
                                    } else {
                                        out.writeUTF("Неверный логин/пароль");
                                    }
                                }
                            }
                        }

                        // цикл работы
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/")){

                                if (str.equals("/end")) {
                                    out.writeUTF("/end");
                                    break;
                                }
                                //
                                if(str.startsWith("/w")){
                                    String[] token = str.split("\\s+", 3);
                                    if(token.length < 3){
                                        continue;
                                    }
                                    server.SimpleMsg(this, token[1], token[2]);
                                }
                                if(str.startsWith("/changeNick")){
                                    String newNick = str.split("\\s", 2)[1];
                                    if(newNick.contains(" ")){
                                        sendMessage("the new name cannot contain spaces");
                                        continue;
                                    }
                                    if(server.getAuthService().changeNickname(this.nickname, newNick)){
                                        this.nickname = newNick;
                                        sendMessage("/changeNick" + nickname);
                                        sendMessage("Name changed");
                                        server.broadCastClientList();
                                    } else{
                                        sendMessage("the name is already taken");
                                    }
                                }
                            } else{
                                server.broadCastMsg(this,str);
                                logger.info("Client " + nickname + " send message");
                                try {
                                    FileReadWrite newFile = new FileReadWrite();
                                    newFile.doUserListWriter(newFile.chatUserMessage(nickname), str);
                                    newFile.doChatWriter(newFile.allChatMessage(), str);
                                } catch (Exception e) {
                                    RuntimeException ex = new RuntimeException("error writing to file");
                                    logger.error("Something went wrong in writing to file", ex);
                                    throw ex;
                                }

                            }
                            //
                        }
                    } catch(SocketTimeoutException e){
                        out.writeUTF("/end");
                    }
                    //
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Client disconnected!");
                    server.unsubscribe(this);
                    logger.info("Client " + nickname + " disconnected");
                    try {
                        socket.close();
                    } catch (EOFException e){
                        System.out.println("End");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
