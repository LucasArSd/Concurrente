
chan Imprimir(unDocumento)
chan ImpresoraDisponible(idImpresora)
chan RealizarImpresion[3](unDocumento)
chan ImprimirDirector(unDocumento)

process Impresora [id: 0..2]{
	text doc;	
	while(true){
		send ImpresoraDisponible(id)
		receive RealizarImpresion[id](doc);
		imprimirDocumento(doc);
	}
}

process Administrativo[id: 0..N-1]{
	text documento;
	while(true){
		documento = realizarDocumento();
		send Imprimir(documento)
	}
}

process Coordinador{
	text documento;
	int idImpresora();
	while (true){
		if (not empty(Imprimir) && not empty(ImpresoraDisponible)){
			receive Imprimir(documento);
			receive ImpresoraDisponible(idImpresora);
			send RealizarImpresion[idImpresora](documento);
		}
	}
}

//B
chan Imprimir(unDocumento)
chan ImpresoraDisponible(idImpresora)
chan RealizarImpresion[3](unDocumento)

process Impresora [id: 0..2]{
	text doc;	
	while(true){
		send ImpresoraDisponible(id)
		receive RealizarImpresion[id](doc);
		imprimirDocumento(doc);
	}
}

process Administrativo[id: 0..N-1]{
	text documento;
	while(true){
		documento = realizarDocumento();
		send Imprimir(documento)
	}
}

process Coordinador{
	text documento;
	int idImpresora();
	while (true){
		if (not empty(ImprimirDirector) && not empty(ImpresoraDisponible)){
			receive ImprimirDirector(documento);
			receive ImpresoraDisponible(idImpresora);
			send RealizarImpresion[idImpresora](documento)
		}else if (not empty(Imprimir) && not empty(ImpresoraDisponible)){
			receive Imprimir(documento);
			receive ImpresoraDisponible(idImpresora);
			send RealizarImpresion[idImpresora](documento);
		}
	}
}

process Director{
	text documento
	while(True){
		documento = realizarDocumento();
		ImprimirDirector(documento);
	}
}

//C

chan Imprimir(text unDocumento);
chan ImprimirDirector(text unDocumento);          // Canal para el director
chan ImpresoraDisponible(int idImpresora);        // Canal para indicar disponibilidad de impresoras
chan RealizarImpresion[3](text unDocumento);      // Canal para asignar documentos a cada impresora
chan Finalizar[3]();                                 // Canal para notificar finalización de trabajo

// Proceso Impresora
process Impresora[id: 0..2] {
    text doc;
    bool continuar = true;

    while (continuar) {
        if (not empty(Finalizar[id])) {               // Revisa si se debe terminar la ejecución
            receive Finalizar[id]();                  // Recibe señal de finalización
            continuar = false;                    // Cambia la variable de control para salir del loop
        } else {
            send ImpresoraDisponible(id);         // Notifica disponibilidad
            receive RealizarImpresion[id](doc);   // Recibe un documento para imprimir
            imprimirDocumento(doc);               // Imprime el documento
        }
    }
}

// Proceso Administrativo
process Administrativo[id: 0..N-1] {
    text documento;
    int trabajos = 10;

    while (trabajos > 0) {
        documento = realizarDocumento();          // Crea un documento
        send Imprimir(documento);                 // Envía el documento al canal de impresión
        trabajos -= 1;                            // Decrementa el contador de trabajos realizados
    }
}

// Proceso Coordinador
process Coordinador {
    text documento;
    int idImpresora;
    int trabajosPendientes = N * 10;              // Total de trabajos a realizar

    while (trabajosPendientes > 0) {
        if (not empty(Imprimir) && not empty(ImpresoraDisponible)) {
            receive Imprimir(documento);           // Recibe un documento de administrativos
            receive ImpresoraDisponible(idImpresora); // Recibe una impresora disponible
            send RealizarImpresion[idImpresora](documento); // Asigna documento a la impresora
            trabajosPendientes -= 1;               // Decrementa trabajos pendientes
        }
    }

    // Envía señal de finalización a todas las impresoras
    for (int i = 0; i < 3; i++) {
        send Finalizar[i]();
    }
}

//D

chan Imprimir(text unDocumento);                   // Canal para documentos de administrativos
chan ImprimirDirector(text unDocumento);           // Canal para documentos del director
chan ImpresoraDisponible(int idImpresora);         // Canal para indicar disponibilidad de impresoras
chan RealizarImpresion[3](text unDocumento);       // Canal para asignar documentos a cada impresora
chan Finalizar();                                  // Canal para notificar finalización de trabajo

// Proceso Impresora
process Impresora[id: 0..2] {
    text doc;
    bool continuar = true;

    while (continuar) {
        if (not empty(Finalizar)) {               // Revisa si se debe terminar la ejecución
            receive Finalizar();                  // Recibe señal de finalización
            continuar = false;                    // Cambia la variable de control para salir del loop
        } else {
            send ImpresoraDisponible(id);         // Notifica disponibilidad
            receive RealizarImpresion[id](doc);   // Recibe un documento para imprimir
            imprimirDocumento(doc);               // Imprime el documento
        }
    }
}

// Proceso Administrativo
process Administrativo[id: 0..N-1] {
    text documento;
    int trabajos = 10;

    while (trabajos > 0) {
        documento = realizarDocumento();          // Crea un documento
        send Imprimir(documento);                 // Envía el documento al canal de impresión
        trabajos -= 1;                            // Decrementa el contador de trabajos realizados
    }
}

// Proceso Director
process Director {
    text documento;
    int trabajos = 10;

    while (trabajos > 0) {
        documento = realizarDocumento();          // Crea un documento
        send ImprimirDirector(documento);         // Envía el documento al canal de impresión
        trabajos -= 1;                            // Decrementa el contador de trabajos realizados
    }
}

// Proceso Coordinador
process Coordinador {
    text documento;
    int idImpresora;
    int trabajosPendientes = N * 10 + 10;         // Total de trabajos a realizar, incluyendo al director

    while (trabajosPendientes > 0) {
        if (not empty(ImprimirDirector) && not empty(ImpresoraDisponible)) {
            receive ImprimirDirector(documento);           // Recibe un documento del director
            receive ImpresoraDisponible(idImpresora);      // Recibe una impresora disponible
            send RealizarImpresion[idImpresora](documento); // Asigna documento a la impresora
            trabajosPendientes -= 1;                       // Decrementa trabajos pendientes
        } else if (not empty(Imprimir) && not empty(ImpresoraDisponible)) {
            receive Imprimir(documento);                   // Recibe un documento de administrativos
            receive ImpresoraDisponible(idImpresora);      // Recibe una impresora disponible
            send RealizarImpresion[idImpresora](documento); // Asigna documento a la impresora
            trabajosPendientes -= 1;                       // Decrementa trabajos pendientes
        }
    }

    // Envía señal de finalización a todas las impresoras
    for (int i = 0; i < 3; i++) {
        send Finalizar();
    }
}


