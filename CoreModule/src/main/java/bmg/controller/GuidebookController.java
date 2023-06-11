package bmg.controller;

import bmg.dto.GuidebookImage;
import bmg.dto.GuidebookImageMetadata;
import bmg.dto.Dimensions;
import bmg.service.GuidebookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @return a List of guidebook images saved in this request in S3
     * @throws IOException
     */
    @PostMapping("/{id}/images")
    public List<GuidebookImage> uploadGuidebookImages(
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
     * @param dimensions The dimensions of the images (WxH)
     * @return a List of guidebook images from AWS S3
     */
    @GetMapping("/{id}/images")
    public List<GuidebookImage> getImagesFromS3(
            @PathVariable(name = "id") String id,
            @RequestParam(name = "dimensions", required = false) String dimensions) {

        Dimensions dim = null;

        if (dimensions != null) {
            dim = parseDimensions(dimensions);
            log.info("Get guidebook images for propertyId={}, dimensions={}", id, dim.toString());
        } else {
            log.info("Get guidebook images for propertyId={}", id);
        }

        return SVC.retrieveGbImagesFromS3(id, dim);
    }

    @GetMapping("/{id}/images/featured")
    public String getFeaturedImageFromS3(
            @PathVariable(name = "id") String id,
            @RequestParam(name = "dimensions", required = false) String dimensions) {

        Dimensions dim = null;

        if (dimensions != null) {
            dim = parseDimensions(dimensions);
            log.info("Get guidebook featured images for propertyId={}, dimensions={}", id, dim.toString());
        } else {
            log.info("Get guidebook featured images for propertyId={}", id);
        }

        return SVC.retrieveGbFeaturedImageFromS3(id, dim);
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

    /**
     * Parses the dimensions from a dimension string
     *
     * @param dimensions A dimension string in format WxH
     * @return An image dimensions object
     */
    private Dimensions parseDimensions(String dimensions) {
        Matcher matcher = Pattern
                .compile("(\\d+.?\\d+)[xX](\\d+.?\\d+)")
                .matcher(dimensions);

        if (matcher.find()) {
            Double width = Double.parseDouble(matcher.group(1));
            Double height = Double.parseDouble(matcher.group(2));
            return new Dimensions(width, height);
        } else {
            String error = String.format("Dimensions=%s is not in the proper format of WxH", dimensions);
            log.error(error);
            throw new IllegalArgumentException(error);
        }
    }
}