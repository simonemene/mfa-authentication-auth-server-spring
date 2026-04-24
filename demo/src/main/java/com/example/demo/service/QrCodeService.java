package com.example.demo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeService {

	public String generateQrCodeBase64(String text) {
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();

			BitMatrix bitMatrix = qrCodeWriter.encode(
					text,
					BarcodeFormat.QR_CODE,
					250,
					250
			);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			MatrixToImageWriter.writeToStream(
					bitMatrix,
					"PNG",
					outputStream
			);

			return Base64.getEncoder().encodeToString(outputStream.toByteArray());

		} catch (Exception ex) {
			throw new IllegalStateException("Cannot generate QR Code", ex);
		}
	}
}