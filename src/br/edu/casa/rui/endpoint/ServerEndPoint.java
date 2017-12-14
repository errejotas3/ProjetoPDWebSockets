package br.edu.casa.rui.endpoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import br.edu.casa.rui.cliente.Cliente;

@ServerEndpoint("/serverendpoint")
public class ServerEndPoint {
	static private HashMap<Integer, Cliente> clients = new HashMap<Integer, Cliente>();
	static private HashMap<Integer, Session> session = new HashMap<Integer, Session>();
	static int mapsize = 0;
	static int idMessage;
	int countFor = 0;
	static Session onOpen;
	static Session onMessage;
	Cliente cliente = new Cliente();
	@OnOpen
	public void handleOpen(Session ses) {
		System.out.println("Novo cliente conectado ao chat");
		onOpen = ses;
		mapsize = mapsize + 1;
		System.out.println(ses.getId());
		cliente.setNome("JonhDoe");
		mapsize = clients.size();
		session.put(mapsize, ses);
		cliente.setPermissao(false);
		clients.put(mapsize, cliente);

		System.out.println("Clientes: " + clients.size());
		System.out.println("SessionSize: " + session.size());
		System.out.println(mapsize);
		try {
			ses.getBasicRemote().sendText("Bem vindo ao chat, favor utilizar o comando /rename");
		} catch (IOException ex) {
			ex.getStackTrace();
		}
	}
	@OnMessage
	public String handleMessage(String message, Session sesmessage) {
		onMessage = sesmessage;
		String recebida;
		for(int i = 0; i <= mapsize; i++) {
			if(onMessage.getId() == session.get(i).getId()) {
				recebida = message;
				verificar(recebida);
			}
		}
		String replyMessage="";
		return replyMessage;
	}
	@OnClose
	public void handleClose(Session onMessage) {
				System.out.println("CHEGOU NO BYE");
				Session close = onMessage;
				int idAux = 0; 
				boolean remove = false;
				String saiu = "";
				for(int i = 0; i < session.size(); i++) {
					System.out.println("Entrpou no for => " + session.get(i).getId());
					if(close.getId().equals(session.get(i).getId())) {
						idAux = i;
						remove = true;
		
						saiu = clients.get(i).getNome();


						System.out.println("Cliente saiu do chat.");
					}
//					else break;
				}
				
				if (remove) {
//					try {
//						session.get(idAux).getBasicRemote().sendText("Você saiu do chat.");
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
					System.out.println("REMOVE DA LISTA");
					clients.remove(idAux);
					session.remove(idAux);
					
					for(int key : session.keySet()) {
						try {
							session.get(key).getBasicRemote().sendText(saiu + " Saiu do chat.");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
	}
	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace();
	}
	@SuppressWarnings("unused")
	String verificar(String verifica) {
		//TRATA A DATA
		Date date = new Date();
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		String dataFormatada = formatador.format(date);
		Calendar data = Calendar.getInstance();
		int horas = data.get(Calendar.HOUR_OF_DAY);
		int minutos = data.get(Calendar.MINUTE);
		//FIM TRATA DATAs
		String aux;
		String finalMessage;
		String[] splited = verifica.split(" ");
		aux = splited[0];

		switch (aux) {
		case ("/send"):
			
			for(int i = 0; i <= mapsize; i++) {
				if(onMessage.getId() == session.get(i).getId()) {
					String userx = clients.get(i).getNome();
//					int fromSesId = clients.get(i).getId();
					if(userx.equals("JonhDoe")) {
						try {
							onMessage.getBasicRemote().sendText("Para entrar no chat utilize o comando /rename");
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}else {
						if (splited.length == 1) {
							try {
								session.get(i).getBasicRemote().sendText("Comando incorreto, exemplo certo /send -all <message> || /send -user <username>");
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						}
						if(splited[1].equals("-all")) {
							
							if (splited.length <= 2) {
								try {
									session.get(i).getBasicRemote().sendText("Comando incorreto, exemplo certo send -all <message>");
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							}
							
							finalMessage = verifica.replace("/send -all", "");
							System.out.println(verifica);
							System.out.println(finalMessage);
							for(int key1 : session.keySet()) {
								try {
									if(key1 == i) {
										session.get(i).getBasicRemote().sendText("Você disse: " +  finalMessage + " - <" + horas + ":" + minutos + "><" + dataFormatada + ">");
									} else {
										if(clients.get(key1).getNome().equals("JonhDoe")) {
											System.out.println("usuario john nao ve nada");
										}else
										session.get(key1).getBasicRemote().sendText(userx + " diz: " + finalMessage + " - <" + horas + ":" + minutos + "><" + dataFormatada + ">");
									}
//									session.get(key1).getBasicRemote().sendText(userx + " diz: " + finalMessage + " - <" + horas + ":" + minutos + "><" + dataFormatada + ">");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else
							if(splited[1].equals("-user")) {
								
								if (splited.length < 3) {
									try {
										session.get(i).getBasicRemote().sendText("Comando incorreto, exemplo certo send -user <username> <message>");
									} catch (IOException e) {
										e.printStackTrace();
									}
									break;
								}
								String usuario = splited[2];
								int tamanho = usuario.length() + 12;
								finalMessage = verifica.substring(tamanho);
								
								boolean noExist = true;
								
								for(int u : clients.keySet()) {
									String resultado = clients.get(u).getNome();
									if(resultado.equals(usuario)) {
										noExist = false;
										Session enviar;
										enviar = session.get(u);
										if (splited.length <= 3) {
											try {
												session.get(i).getBasicRemote().sendText("Comando incorreto, exemplo certo send -user <username> <message>");
											} catch (IOException e) {
												e.printStackTrace();
											}
											break;
										}
										
										try {
											enviar.getBasicRemote().sendText(userx + " diz: " + finalMessage + " - <" + horas + ":" + minutos + "><" + dataFormatada + ">");
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
								
								if (noExist) {
									try {
										session.get(i).getBasicRemote().sendText(usuario + "não existe!");
									} catch (IOException e) {
										e.printStackTrace();
									}
				
								}
								
								if (!noExist) {
									try {
										session.get(i).getBasicRemote().sendText("Você disse: " +  finalMessage + " - <" + horas + ":" + minutos + "><" + dataFormatada + ">");
									} catch (IOException e) {
										e.printStackTrace();
									}
				
								}
							}								
					}
				}
			}
		break;

		case("/list"):

			String result;
		try {
			onMessage.getBasicRemote().sendText("<--Usuários Conectados-->");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int key : clients.keySet()) {
			result = clients.get(key).getNome();

			if(result.equals("JonhDoe")) {
				System.out.println(result);
			}else {

				try {
					onMessage.getBasicRemote().sendText(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		break;

		case("/rename"):
			String newName = splited[1];
		for(int i = 0; i <= mapsize; i++) {

			if(clients.get(i).getNome().equals(newName)) {
				try {
					onMessage.getBasicRemote().sendText(newName + " Já está em uso, favor escolher outro nome.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}else

				if(onMessage.getId() == session.get(i).getId()) {
					String teste = clients.get(i).getNome();
					if(teste.equals("JonhDoe")) {
						for(int key : session.keySet()) {
							try {
								if(clients.get(key).getNome().equals("JonhDoe")) {
									System.out.println("jonh é um zé ngm");
								}else
								session.get(key).getBasicRemote().sendText(newName + " Entrou no chat");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
					else {
						for(int key : session.keySet()) {
							try {
								session.get(key).getBasicRemote().sendText(teste + " agora é: " + newName);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
					clients.get(i).setNome(newName);
					clients.get(i).setPermissao(true);
					teste = clients.get(i).getNome();

					break;
				}
		}
		break;
		case("/bye"):

			this.handleClose(onMessage);
		break;
		default:
			try {
				onMessage.getBasicRemote().sendText("Comando inválido, utilizar apenas os comandos seguintes:");
				onMessage.getBasicRemote().sendText("/send -all(envia mensagem para a sala)");
				onMessage.getBasicRemote().sendText("/send -user(enviar mensagem para um usuario especifico)");
				onMessage.getBasicRemote().sendText("/bye (sair do grupo)");
				onMessage.getBasicRemote().sendText("/list (listar usuários na sala)");
				onMessage.getBasicRemote().sendText("/rename (nome) para renomear");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		return aux;
	}
}