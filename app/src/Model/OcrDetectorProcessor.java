public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
	private GraphicOverlay<OcrGraphic> graphicOverlay;

	OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
		graphicOverlay = ocrGraphicOverlay;
	}
}

@Override
public void receiveDetections(Detector.Detections<TextBlock> detections) {
	graphicOverlay.clear();
	SparseArray<TextBlock> items = detections.getDetectedItems();
	for (int i = 0; i < items.size(); ++i) {
		TextBlock item = items.valueAt(i);
		if (item != null && item.getValue() != null) {
			Log.d("Processor", "Text detected! " + item.getValue());
			OcrGraphic graphic = new OcrGraphic(graphicOverlay, item);
			graphicOverlay.add(graphic);
		}
	}
}
@Override

public void release() {
	graphicOverlay.clear();
}