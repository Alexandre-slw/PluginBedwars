package com.alexandre.bedwars.utils;

public class SimpleAnimation {
	private Long startMs;
	private Long duration;

	public float start;
	public float end;
	
	public float initialStart;
	public float initialEnd;
	public Long initialDuration;

	public enum Type {
		LINEAR,
		SINE_EASE_IN, SINE_EASE_OUT, SINE_EASE_IN_OUT,
		QUAD_EASE_IN, QUAD_EASE_OUT, QUAD_EASE_IN_OUT,
		CUBIC_EASE_IN, CUBIC_EASE_OUT, CUBIC_EASE_IN_OUT,
		QUART_EASE_IN, QUART_EASE_OUT, QUART_EASE_IN_OUT,
		QUINT_EASE_IN, QUINT_EASE_OUT, QUINT_EASE_IN_OUT,
		EXPO_EASE_IN, EXPO_EASE_OUT, EXPO_EASE_IN_OUT,
		CIRC_EASE_IN, CIRC_EASE_OUT, CIRC_EASE_IN_OUT,
		BACK_EASE_IN, BACK_EASE_OUT, BACK_EASE_IN_OUT,
		ELASTIC_EASE_IN, ELASTIC_EASE_OUT, ELASTIC_EASE_IN_OUT,
		BOUNCE_EASE_IN, BOUNCE_EASE_OUT, BOUNCE_EASE_IN_OUT;
	}

	public Type type;

	public SimpleAnimation(Long duration, float start, float end) {
		this(duration, start, end, Type.LINEAR);
	}

	public SimpleAnimation(Long duration, float start, float end, Type type) {
		this.duration = duration;
		this.start = start;
		this.end = end;
		this.type = type;
		
		this.initialStart = this.start;
		this.initialEnd = this.end;
		this.initialDuration = this.duration;
		this.startMs = this.getTime();
	}

	public long getTime() {
		return System.currentTimeMillis();
	}

	public static SimpleAnimation createAnimationFromSpeedBySeconds(float speed, float start, float end, Type type) {
		float range = Math.max(end, start) - Math.min(end, start);
		long duration = Math.round((range / speed) * 1000);
		return new SimpleAnimation(duration, start, end, type);
	}
	
	public float getValue() {
		if (this.end - this.start == 0) return end;
		if (this.isFinished()) return this.end;
		
		float time = (float) (this.getTime() - this.startMs);
		float progress = time / (float) this.duration;
		return SimpleAnimation.getValue(this.start, this.end, progress, this.type);
	}
	
	public float getPercent() {
		if (this.end - this.start == 0) return end;
		if (this.isFinished()) return this.end;
		
		float time = (float) (this.getTime() - this.startMs);
		float progress = time / (float) this.duration;
		return SimpleAnimation.getValue(this.start, this.end, progress, this.type) / this.end;
	}
	
	public static float getValue(float start, float end, float progress, Type type) {
		if (end - start == 0) return end;
		if (progress >= 1f) return end;
		
		float change = (float) (end - start);
		switch (type) {
			case LINEAR:
				break;
			
			// SINE
			
			case SINE_EASE_IN:
				progress = (float) (1 - Math.cos((progress * Math.PI) / 2f));
				break;
				
			case SINE_EASE_OUT:
				progress =  (float) Math.sin((progress *  Math.PI) / 2f);
				break;
				
			case SINE_EASE_IN_OUT:
				progress =  (float) Math.sin((progress *  Math.PI) / 2f);
				break;
				
			// QUAD
			
			case QUAD_EASE_IN:
				progress = progress * progress;
				break;
				
			case QUAD_EASE_OUT:
				progress = 1 - (1 - progress) * (1 - progress);
				break;
				
			case QUAD_EASE_IN_OUT:
				progress = (float) (progress < 0.5 ? 2 * progress * progress : 1 - Math.pow(-2 * progress + 2, 2) / 2f);
				break;
				
			// CUBIC
			
			case CUBIC_EASE_IN:
				progress = progress * progress * progress;
				break;
				
			case CUBIC_EASE_OUT:
				progress = (float) (1 - Math.pow(1 - progress, 3));
				break;
				
			case CUBIC_EASE_IN_OUT:
				progress = (float) (-(Math.cos(Math.PI * progress) - 1) / 2f);
				break;
				
			// QUART
				
			case QUART_EASE_IN:
				progress = progress * progress * progress * progress;
				break;
				
			case QUART_EASE_OUT:
				progress = (float) (1 - Math.pow(1 - progress, 4));
				break;
				
			case QUART_EASE_IN_OUT:
				progress = (float) (progress < 0.5 ? 8 * progress * progress * progress * progress : 1 - Math.pow(-2 * progress + 2, 4) / 2f);
				break;
				
			// QUINT
				
			case QUINT_EASE_IN:
				progress = progress * progress * progress * progress * progress;
				break;

			case QUINT_EASE_OUT:
				progress = (float) (1 - Math.pow(1 - progress, 5));
				break;

			case QUINT_EASE_IN_OUT:
				progress = (float) (progress < 0.5 ? 16 * progress * progress * progress * progress * progress : 1 - Math.pow(-2 * progress + 2, 5) / 2f);
				break;
				
			// EXPO
				
			case EXPO_EASE_IN:
				progress = (float) (progress == 0 ? 0 : Math.pow(2, 10 * progress - 10));
				break;

			case EXPO_EASE_OUT:
				progress = (float) (progress == 1 ? 1 : 1 - Math.pow(2, -10 * progress));
				break;

			case EXPO_EASE_IN_OUT:
				progress = (float) (progress == 0 ? 
							0 : progress == 1 ? 
							1 : progress < 0.5 ?
							Math.pow(2, 20 * progress - 10) / 2f : (2 - Math.pow(2, -20 * progress + 10)) / 2f);
				break;
				
			// CIRC
				
			case CIRC_EASE_IN:
				progress = (float) (1 - Math.sqrt(1 - Math.pow(progress, 2)));
				break;

			case CIRC_EASE_OUT:
				progress = (float) Math.sqrt(1 - Math.pow(progress - 1, 2));
				break;

			case CIRC_EASE_IN_OUT:
				progress = (float) (progress < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * progress, 2))) / 2f : (Math.sqrt(1 - Math.pow(-2 * progress + 2, 2)) + 1) / 2f);
				break;
				
			// BACK
				
			case BACK_EASE_IN:
				float c1 = 1.70158f;
				float c2 = c1 + 1;

				progress = c2 * progress * progress * progress - c1 * progress * progress;
				break;

			case BACK_EASE_OUT:
				float c3 = 1.70158f;
				float c4 = c3 + 1;

				progress = (float) (1 + c4 * Math.pow(progress - 1, 3) + c3 * Math.pow(progress - 1, 2));
				break;
				
			case BACK_EASE_IN_OUT:
				float c5 = 1.70158f;
				float c6 = c5 * 1.525f;

				progress = (float) (progress < 0.5
				  ? (Math.pow(2 * progress, 2) * ((c6 + 1) * 2 * progress - c6)) / 2f
				  : (Math.pow(2 * progress - 2, 2) * ((c6 + 1) * (progress * 2 - 2) + c6) + 2) / 2f);
				break;
				
			// ELASTIC
				
			case ELASTIC_EASE_IN:
				float c7 = (float) ((2 * Math.PI) / 3f);

				progress = (float) (progress == 0 ?
						0 : progress == 1 ?
						1 : -Math.pow(2, 10 * progress - 10) * Math.sin((progress * 10 - 10.75) * c7));
				break;
				
			case ELASTIC_EASE_OUT:
				float c8 = (float) ((2 * Math.PI) / 3f);

				progress = (float) (progress == 0 ?
							0 : progress == 1 ?
							1 : Math.pow(2, -10 * progress) * Math.sin((progress * 10 - 0.75) * c8) + 1);
				break;
				
			case ELASTIC_EASE_IN_OUT:
				float c9 = (float) (2 * Math.PI) / 4.5f;

				progress = (float) (progress == 0 ?
							0 : progress == 1 ?
							1 : progress < 0.5 ?
							-(Math.pow(2, 20 * progress - 10) * Math.sin((20 * progress - 11.125) * c9)) / 2 : (Math.pow(2, -20 * progress + 10) * Math.sin((20 * progress - 11.125) * c9)) / 2f + 1);
				break;
				
			// BOUNCE
				
			case BOUNCE_EASE_IN:
				progress = 1 - easeOutBounce(1 - progress);
				break;
				
			case BOUNCE_EASE_OUT:
				progress = easeOutBounce(progress);
				break;
				
			case BOUNCE_EASE_IN_OUT:
				progress = progress < 0.5 ? (1 - easeOutBounce(1 - 2 * progress)) / 2f : (1 + easeOutBounce(2 * progress - 1)) / 2f;
				break;
		}

		return start + change * progress;
	}
	
	private static float easeOutBounce(float progress) {
		float n1 = 7.5625f;
		float d1 = 2.75f;

		if (progress < 1 / d1) return n1 * progress * progress;
		else if (progress < 2 / d1) return (float) (n1 * (progress -= 1.5 / d1) * progress + 0.75);
		else if (progress < 2.5 / d1) return (float) (n1 * (progress -= 2.25 / d1) * progress + 0.9375);
		else return (float) (n1 * (progress -= 2.625 / d1) * progress + 0.984375);
	}

	public boolean isFinished() {
		return this.getTime() - this.startMs >= this.duration;
	}
	
	public void restart() {
		this.startMs = this.getTime();
	}
	
	public void reverse() {
		float value = this.getValue();
		float start = this.initialStart;
		float end = this.initialEnd;
		
//		float s = this.start;
//		float e = this.end;
		
		this.start = value;
		this.end = start;
		
		this.initialEnd = start;
		this.initialStart = end;

		float percent = 0f;
		if (!this.isFinished()) {
			float total = Math.max(start, end) - Math.min(start, end);
			float distance = Math.max(end, value) - Math.min(end, value);
			percent = distance / total;
		}
		
		this.duration = this.initialDuration - Math.round(this.initialDuration * percent);
		this.startMs = this.getTime();
	}
}