chan PedidoCliente(idCliente, pedido);
chan AtencionPedido(idCliente, pedido);
chan EntregaPedido[N]();

//canales para realizar la comunicacion entre el coordinador y el vendedor
chan RecepcionDePedido[3](idCliente, pedido);
chan SolicitudDePedido(idVendedor);

process cocinero[id: 0..1]{
	int idCliente;
	text pedido;
	while(true){
		receive AtencionPedido(idCliente, pedido);
		pedido = cocinarPedido();
		send EntregaPedido[idCliente](pedido);
	}
}

process vendedor[id: 0..2]{
	int idCliente;
	text pedido;
	int espera;
	while(true){
		send SolicitudDePedido(id);
		receive RecepcionDePedido[id](idCliente, pedido);
		if (pedido != "vacio"){
			send AtencionPedido(idCliente, pedido); //envía pedido a cocinero
		}else{
			espera = Rand(2000)+1000;
			delay(espera); //se queda de 1 a 3 minutos reponiendo el pack de bebidas
		}		
	}
}

process cliente[id: 0..N-1]{
	text pedido;
	//realiza pedido a vendedor
	send PedidoCliente(id, pedido);
	//espera a recibir la entrega
	receive EntregaPedido[id](pedido); 
	
}

process CoordinadorVendedores(){ //necesario coordinador para evitar condición de carrera entre los mismos
	int idCliente, idVendedor;  
	text pedido;
	while(true){
		receive SolicitudDePedido(idVendedor);
		if (not empty(PedidoCliente)){
			receive PedidoCliente(idCliente, pedido);
		}else{
			idCliente = -1;
			pedido = "vacio";
		}
		send RecepcionDePedido[idVendedor](idCliente, pedido);
	}
}
