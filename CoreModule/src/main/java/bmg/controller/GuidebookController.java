package bmg.controller;

import bmg.dto.Guidebook;
import bmg.model.Property;
import bmg.service.GuidebookService;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/guidebook")
@RequiredArgsConstructor
public class GuidebookController extends Controller<Guidebook> {

    private final GuidebookService SVC;

    @PostMapping("/{id}")
    public String saveGuidebook(@PathVariable(name = "id") String id, @RequestBody Guidebook gb) {
        return SVC.saveToS3(id, gb);
    }

    @GetMapping("/{id}")
    public Guidebook retrieveGuidebook(@PathVariable(name = "id") String id) throws IOException {
        return SVC.retrieveFromS3(id);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<PutObjectResult> saveGuidebookByPropertyID(@PathVariable(name = "id") String id, @RequestBody String jsonPayload) {
//        PutObjectResult guidebookResult = SVC.saveGuidebookJsonInfo(id, jsonPayload);
//        HttpStatus status = HttpStatus.OK;
////        return responseEntity(status, status.name(), data, null);
//        return responseCodeOk(List.of(guidebookResult));
//    }

//    @GetMapping("/{id}")
//    public void getGuidebookByPropertyID(@PathVariable(name = "id") String id, @RequestBody List<Property> properties) {
//        Property property = SVC.retrieveGuidebookJsonInfo(id, ResponseBody JsonObject);
//        return responseCodeOk(List.of(property));
  //  }


    // write endpoint that does the following:
            // call respective method in guidebook service that does:
                        //connect to S3
                        // retrieve json by propertyID (name of property object)
                        // return that json in the response endpoint
    // in frontend
    // parse text into json obj
    // pass to Guidebook Loader to display to the page

}