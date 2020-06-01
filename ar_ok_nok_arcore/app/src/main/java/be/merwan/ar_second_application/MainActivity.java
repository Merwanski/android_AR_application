package be.merwan.ar_second_application;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArFragment arFragment;
    private ImageView  fitToScanView;

    // NOT NEEDED
    // private ModelRenderable ok_mark, ok_markb;
    /// private ModelRenderable not_ok_mark, not_ok_markb;

    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        fitToScanView = findViewById(R.id.image_view_fit_to_scan);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);


    }

    @Override
    protected void onResume() {
        super.onResume();
        arFragment.getArSceneView().getPlaneRenderer().setVisible(false);
        if (augmentedImageMap.isEmpty()){
            fitToScanView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Registered with the Sceneform Scene object, this method is called at the start of each frame.
     *
     * @param frameTime - time since last frame.
     */
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        Collection<AugmentedImage> updateAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage augmentedImage:updateAugmentedImages){
            switch (augmentedImage.getTrackingState()){
                case PAUSED:
                    // When an image is in PAUSED state, but the camera  is note PAUSED, it has been detected
                    // but not yet tracked
                    String text = "Detected Image " + augmentedImage.getIndex();
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View
                    fitToScanView.setVisibility(View.GONE);

                    // Create a new anchor for newly found images
                    if (!augmentedImageMap.containsKey(augmentedImage)){
                        AugmentedImageNode node = new AugmentedImageNode(this);
                        node.setImage(augmentedImage);
                        augmentedImageMap.put(augmentedImage, node);
                        arFragment.getArSceneView().getScene().addChild(node);
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }

    }

}
