package bmg.controller;

import bmg.dto.GuidebookImageMetadata;
import bmg.service.GuidebookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/guidebook")
@RequiredArgsConstructor
@Log4j2
public class GuidebookController extends Controller<Object> {

    private final GuidebookService SVC;

    /**
     * POST request for /api/guidebook/PID#######/images
     * @param id PropertyID
     * @param gb JSON file of type Guidebook. Includes optional and required fields
     * @return a String denoting if saved successfully to S3
     */
    @PostMapping("/{id}/content")
    public String saveGuidebookContent(@PathVariable(name = "id") String id, @RequestBody Object gb) {
        log.info("Save guidebook for propertyId={}:", id);
        log.info("\tgb={}", gb);

        return SVC.saveGbContentToS3(id, gb);
    }

    /**
     * GET request for /api/guidebook/PID#######/content
     * @param id PropertyID
     * @return a Guidebook JSON object for that particular property
     * @throws IOException
     */
    @GetMapping("/{id}/content")
    public Object retrieveGuidebookContent(@PathVariable(name = "id") String id) throws IOException {
        log.info("Get guidebook for propertyId={}", id);
        return SVC.retrieveGbContentFromS3(id);
    }

    /**
     * POST request for /api/guidebook/PID#######/images
     * When making a request, set the 'Content-Type' Header, to 'multipart/form-data'
     * with form-data as body, with 'files' as Key, and Value 'MultipartFile' (Mult. image files)
     * @param id PropertyID
     * @param files MultiPartFile of multiple image files
     * @return a List of URLs for the images saved in this request in S3
     * @throws IOException
     */
    @PostMapping("/{id}/images")
    public List<URL> uploadGuidebookImages(
            @PathVariable(name = "id") String id,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("metadata") GuidebookImageMetadata[] metadata) throws IOException {

        log.info("Upload guidebook images for propertyId={}:", id);
        log.info("\tFiles:");

        for (MultipartFile file : files)
            log.info("\t\tsize={}, content-type={}, name={}", file.getSize(), file.getContentType(), file.getOriginalFilename());

        for (GuidebookImageMetadata meta : metadata)
            log.info("\t\tcustomFileName={}, tags={}", meta.getName(), meta.getTags());

        return SVC.saveGbImagesToS3(id, files, metadata);
    }

    /**
     * GET request for /api/guidebook/PID#######/images
     * @param id PropertyID
     * @return a List of presigned URL strings from AWS S3
     */
    @GetMapping("/{id}/images")
    public List<String> getImagesFromS3(@PathVariable(name = "id") String id) {
        log.info("Get guidebook images for propertyId={}", id);
        return SVC.retrieveGbImagesFromS3(id);
    }

    /**
     * DELETE request for /api/guidebook/PID#######/delete
     * @param id PropertyID
     */
    @GetMapping("/{id}/delete")
    public void deleteGuidebook(@PathVariable(name = "id") String id) {
        log.info("Delete guidebook for propertyId={}", id);
        SVC.deleteGuidebook(id);
    }

    @DeleteMapping("/images")
    public ResponseEntity<Response<Object>> deleteImage(@RequestParam(name = "url") String url) {
        log.info("Delete image url={}", url);
        SVC.deleteGuidebookImage(url);
        return responseCodeNoContent();
    }
}