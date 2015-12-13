package edu.franklin.practicum.f15.strategygame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class AssetLoader implements Disposable, AssetErrorListener {

    private static final String TAG = AssetLoader.class.getName();
    private static AssetLoader theInstance;
    private AssetManager manager;


    private AssetLoader() {

    }

    public static AssetLoader instance() {

        if (null == theInstance) {

            theInstance = new AssetLoader();
        }

        return theInstance;
    }
    
    public void init(AssetManager assetmanager) {
        this.manager = assetmanager;
        assetmanager.setErrorListener(this);
//loading terrain file
        this.manager.load("terrain/GrassQuick.png", Texture.class);
        this.manager.load("terrain/Grass.png", Texture.class);
        this.manager.load("terrain/WaterQuick2.png", Texture.class);
        this.manager.load("terrain/Swamp.png", Texture.class);
        this.manager.load("terrain/SandQuick.png", Texture.class);
        this.manager.load("terrain/Rock.png", Texture.class);
        this.manager.load("terrain/Forest.png", Texture.class);
        this.manager.load("terrain/MountainQuick.png", Texture.class);
        this.manager.load("terrain/Fire.png", Texture.class);
        this.manager.load("terrain/FogOfWarHidden.png", Texture.class);
        
        this.manager.load("terrain/FogOfWarStale.png", Texture.class);
        this.manager.load("terrain/hexes.png", Texture.class);
        this.manager.load("terrain/hextilesets2.png", Texture.class);

//loading Sprites file
        this.manager.load("sprites/dogconcept.png", Texture.class);
        this.manager.load("sprites/EndPoint.png", Texture.class);
        this.manager.load("sprites/FoundREPoint.png", Texture.class);
        this.manager.load("sprites/ItemPoint.png", Texture.class);
        this.manager.load("sprites/MidPoint.png", Texture.class);
        this.manager.load("sprites/REPoint.png", Texture.class);
        this.manager.load("sprites/robot_src_128px.png", Texture.class);
        this.manager.load("sprites/robot_src_hi_res.png", Texture.class);
        this.manager.load("sprites/RobotN.png", Texture.class);
        this.manager.load("sprites/RobotNE.png", Texture.class);
        this.manager.load("sprites/RobotNW.png", Texture.class);
        this.manager.load("sprites/RobotS.png", Texture.class);
        this.manager.load("sprites/RobotSe.png", Texture.class);
        this.manager.load("sprites/RobotSide.png", Texture.class);
        this.manager.load("sprites/RobotSide128Hex.png", Texture.class);
        this.manager.load("sprites/RobotSW.png", Texture.class);
        this.manager.load("sprites/StartPoint.png", Texture.class);
        this.manager.load("sprites/UsedItemPoint.png", Texture.class);

//loading logo file
        this.manager.load("logo/badlogic.jpg", Texture.class);
        this.manager.load("logo/Default-568h@2x.png", Texture.class);
        this.manager.load("logo/Default.png", Texture.class);
        this.manager.load("logo/Default@2x.png", Texture.class);
        this.manager.load("logo/Default@2x~ipad.png", Texture.class);
        this.manager.load("logo/Default~ipad.png", Texture.class);

        
        
        this.manager.finishLoading();

    }


    @Override
    public void dispose() {

        this.manager.dispose();

    }



    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {

        Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'",
                throwable);

    }
}

