import os
import urllib.request
import json
import math
import struct
import wave

# Sound output directory
TARGET_DIR = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "sounds")
os.makedirs(TARGET_DIR, exist_ok=True)

# Wikimedia Commons direct audio URLs (Public Domain / CC0 / CC-BY)
AUDIO_SOURCES = {
    "gentle_rain.ogg": "https://upload.wikimedia.org/wikipedia/commons/2/23/Rain_%281%29.ogg",
    "night_waves.ogg": "https://upload.wikimedia.org/wikipedia/commons/e/e6/Waves.ogg",
    "fireplace.ogg": "https://upload.wikimedia.org/wikipedia/commons/0/07/Campfire_sound_ambience.ogg",
    "forest_creek.ogg": "https://upload.wikimedia.org/wikipedia/commons/e/ea/Brook_sound.ogg",
    "campfire.ogg": "https://upload.wikimedia.org/wikipedia/commons/6/6c/Campfire_ambience.ogg",
    "tent_rain.ogg": "https://upload.wikimedia.org/wikipedia/commons/3/36/Sound_of_rain.ogg",
    "night_crickets.ogg": "https://upload.wikimedia.org/wikipedia/commons/8/87/Crickets_choir.ogg",
    "waterfall.ogg": "https://upload.wikimedia.org/wikipedia/commons/2/2a/Waterfall.ogg",
    "wind_breeze.ogg": "https://upload.wikimedia.org/wikipedia/commons/8/88/Breeze.ogg",
    "cafe_ambience.ogg": "https://upload.wikimedia.org/wikipedia/commons/d/df/Restaurant_ambience.ogg",
    "library_ambience.ogg": "https://upload.wikimedia.org/wikipedia/commons/0/07/Quiet_room_ambience.ogg",
    "window_rain.ogg": "https://upload.wikimedia.org/wikipedia/commons/b/b3/Rain_falling_on_leaves.ogg",
    "calm_stream.ogg": "https://upload.wikimedia.org/wikipedia/commons/d/da/Rivernoise.ogg",
    "wood_stove.ogg": "https://upload.wikimedia.org/wikipedia/commons/1/14/Fireplace_sound.ogg",
    "forest_wind.ogg": "https://upload.wikimedia.org/wikipedia/commons/0/02/Wind_blowing_through_trees.ogg",
    "green_noise.ogg": "https://upload.wikimedia.org/wikipedia/commons/c/c5/Pink_noise.ogg"
}

headers = {
    'User-Agent': 'HushWhiteNoiseApp/1.0 (Educational open source white noise project; yunchang.lee)'
}

def generate_fallback_ambient_wav(filename, sound_type, duration_sec=5, sample_rate=22050):
    """Fallback generator for ambient texture if remote download is temporarily unreachable."""
    wav_path = os.path.join(TARGET_DIR, filename)
    num_samples = int(duration_sec * sample_rate)
    
    import random
    samples = []
    
    last_val = 0.0
    b0 = b1 = b2 = b3 = b4 = b5 = b6 = 0.0
    
    for i in range(num_samples):
        t = i / sample_rate
        white = random.uniform(-1.0, 1.0)
        
        if "rain" in sound_type:
            # Low-pass filtered noise + occasional drops
            last_val = 0.92 * last_val + 0.08 * white
            sample = last_val * 0.8
            if random.random() < 0.002:
                sample += random.uniform(-0.4, 0.4)
        elif "wave" in sound_type:
            # Swelling noise envelope simulating wave cycles (approx 8s period)
            envelope = (math.sin(2 * math.pi * 0.125 * t) + 1.0) * 0.5
            last_val = 0.85 * last_val + 0.15 * white
            sample = last_val * envelope * 0.9
        elif "fire" in sound_type:
            # Brown noise + sporadic sharp crackles
            last_val = (last_val + (0.02 * white)) / 1.02
            sample = last_val * 3.5
            if random.random() < 0.003:
                sample += random.uniform(-0.7, 0.7)
        elif "water" in sound_type or "creek" in sound_type or "stream" in sound_type:
            # Gurgling bandpass
            last_val = 0.7 * last_val + 0.3 * white
            sample = last_val * (0.6 + 0.4 * math.sin(2 * math.pi * 3.0 * t))
        elif "wind" in sound_type:
            # Slow surging low-pass
            mod = 0.5 + 0.5 * math.sin(2 * math.pi * 0.2 * t)
            last_val = (last_val + (0.03 * white)) / 1.03
            sample = last_val * 3.0 * mod
        elif "cricket" in sound_type:
            # High frequency bursts (chirps around 4500Hz)
            chirp = math.sin(2 * math.pi * 4600.0 * t)
            mod = 1.0 if (int(t * 8) % 2 == 0) and (int(t * 40) % 2 == 0) else 0.0
            sample = chirp * mod * 0.3 + 0.05 * white
        else:
            # Ambient diffuse noise
            b0 = 0.99886 * b0 + white * 0.0555179
            b1 = 0.99332 * b1 + white * 0.0750759
            b2 = 0.96900 * b2 + white * 0.1538520
            b3 = 0.86650 * b3 + white * 0.3104856
            b4 = 0.55000 * b4 + white * 0.5329522
            b5 = -0.7616 * b5 - white * 0.0168980
            pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
            b6 = white * 0.115926
            sample = pink * 0.12
            
        sample = max(-1.0, min(1.0, sample))
        int_sample = int(sample * 32767)
        samples.append(int_sample)
        
    with wave.open(wav_path, "wb") as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        raw_data = struct.pack(f"<{len(samples)}h", *samples)
        wf.writeframes(raw_data)
    print(f"Generated procedural fallback ambient track: {filename}")

def download_or_generate():
    for filename, url in AUDIO_SOURCES.items():
        dest = os.path.join(TARGET_DIR, filename)
        if os.path.exists(dest) and os.path.getsize(dest) > 1000:
            print(f"Already exists: {filename}")
            continue
            
        print(f"Downloading {filename} from {url}...")
        try:
            req = urllib.request.Request(url, headers=headers)
            with urllib.request.urlopen(req, timeout=10) as response, open(dest, 'wb') as out_file:
                data = response.read()
                out_file.write(data)
            print(f"Successfully downloaded {filename} ({len(data)} bytes)")
        except Exception as e:
            print(f"Download failed for {filename} ({e}). Generating high-quality ambient procedural fallback audio...")
            generate_fallback_ambient_wav(filename, filename)

if __name__ == "__main__":
    download_or_generate()
