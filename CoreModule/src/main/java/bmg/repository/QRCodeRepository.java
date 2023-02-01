package bmg.repository;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.net.URL;
import java.util.Date;

/**
 * Provides CRUD operations for QR codes
 */
@Repository
@RequiredArgsConstructor
public class QRCodeRepository {

    private final AmazonS3 S3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private static final String QR_FOLDER = "inviteQR/";

    /**
     * Checks if a QR code with the given id exists
     *
     * @param id A QR code id
     * @return True if a QR code with the given id exists, or false otherwise
     */
    public boolean qrCodeExists(String id) {
        return S3.doesObjectExist(bucket, id);
    }

    /**
     * Saves the given QR code file under the given id
     *
     * @param id A QR code id
     * @param qrCode A file containing a QR code
     */
    public void saveOne(String id, File qrCode) {
        S3.putObject(bucket, QR_FOLDER+id, qrCode);
    }

    /**
     * Deletes the QR code with the given id
     *
     * @param id A QR code id
     */
    public void deleteOne(String id) {
        S3.deleteObject(bucket, QR_FOLDER+id);
    }

    /**
     * Gets a temporary URL for the identified QR code
     *
     * @param id A QR code id
     * @param expiration The expiration date of the URL
     * @return A temporary URL
     */
    public URL getUrl(String id, Date expiration) {
        return S3.generatePresignedUrl(
                new GeneratePresignedUrlRequest(bucket, QR_FOLDER+id)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration));
    }
}
