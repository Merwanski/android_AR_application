package be.merwan.tuto_ar;

import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;



public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable redCubeRenderable;
    private ModelRenderable mon_3d_modele;
    private ModelRenderable helicopter_3d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        /*
        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(
                        material-> {
                            redCubeRenderable=
                                    ShapeFactory.makeCube(new Vector3(0.2f, 0.2f, 0.2f), new Vector3(0.0f, 0.15f, 0.0f), material);});
        */

        ModelRenderable.builder()
                .setSource(this, Uri.parse("Helicopter.sfb"))
                .build()
                .thenAccept(renderable -> helicopter_3d = renderable);


        arFragment.setOnTapArPlaneListener(
                // Create the "anchor" of the 3D Object by clicking on the screen
                // Attach the 3D Model to the created anchor
                this::onTapPlane
        );

    }

    private void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (helicopter_3d == null) {
            return;
        }
        if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
            return;
        }

        // Create the "anchor" of the 3D Object by clicking on the screen
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Attach the 3D Model to the created anchor
        TransformableNode Node = new TransformableNode(arFragment.getTransformationSystem());
        Node.setParent(anchorNode);
        Node.setRenderable(helicopter_3d);
        Node.select();
    }

}
