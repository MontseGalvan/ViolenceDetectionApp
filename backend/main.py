import os
import re
import string
import pickle
import subprocess
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import spacy
import nltk
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import TfidfVectorizer 

nltk.download("stopwords")

# Definir stopwords, excluyendo pronombres y palabras clave
stop_words = set(stopwords.words("spanish"))
pronombres = {
    "yo", "tú", "vos", "él", "ella", "ello", "nosotros", "nosotras",
    "vosotros", "vosotras", "ellos", "ellas", "usted", "ustedes",
    "me", "te", "se", "nos", "os", "lo", "la", "le", "los", "las", "les",
    "mi", "mis", "tu", "tus", "su", "sus", "nuestro", "nuestra", "nuestros", "nuestras",
    "vuestro", "vuestra", "vuestros", "vuestras",
    "este", "esta", "estos", "estas", "ese", "esa", "esos", "esas",
    "aquel", "aquella", "aquellos", "aquellas",
    "que", "quien", "quienes", "cuyo", "cuya", "cuyos", "cuyas", "cual", "cuales"
}
palabras_a_mantener = {"no", "ni", "nunca", "nada", "tampoco", "sin"} | pronombres
stop_words = stop_words - palabras_a_mantener

# Cargar modelo de spaCy (si no existe, descargar)
try:
    nlp = spacy.load("es_core_news_sm")
except OSError:
    subprocess.run(["python", "-m", "spacy", "download", "es_core_news_sm"])
    nlp = spacy.load("es_core_news_sm")

# Diccionarios de normalización (jerga, emojis, etc.)
term_estandar = {
    "lol": "reir", "xd": "reir", "xdd": "reir",
    "fb": "facebook", "face": "facebook",
    "ig": "instagram", "insta": "instagram",
    "wpp": "whatsapp", "whats": "whatsapp", "wa": "whatsapp", "wats": "whatsapp",
    "msj": "mensaje", "md": "mensaje directo", "dm": "mensaje directo",
    "xq": "porque", "pq": "porque", "pqt": "porque", "porq": "porque",
    "tmb": "tambien", "tb":"tambien","dnd":"donde",
    "q": "que", "k": "que",
    "d": "de",
    "x": "por",
    "t": "te", "m": "me",
    "hdp": "hijo de puta", "hp": "hijo de puta", "pt": "puta", "pta": "puta",
    "ctm":"chinga tu madre", "ptm":"puta madre", "vtlv":"vete a la verga", "pndj":"pendejo",
    "wey": "persona", "we": "persona", "profe":"profesor", "prof":"profesor",
    "sr":"señor", "dr":"doctor", "facu":"facultad", "uni":"universidad",
    "nudes":"fotos sexuales", "pack":"fotos sexuales", "stalkear":"acosar",
    "xfa": "por favor", "depto":"departamento", "etc":"etcetera"
}

terminosEmoji = {
    "reir": ["\U0001F923", "\U0001F602"],
    "tristeza": ["\U0001F972", "\U0001F614", "\U0001F62A","\U0001F641", "\U00002639", "\U0001F622", "\U0001F62D", "\U0001F61E", "\U0001F63F", "\U0001F494"],
    "miedo": ["\U0001F628", "\U0001F631", "\U0001F630"],
    "enojo": ["\U0001F624", "\U0001F621", "\U0001F620", "\U0001F47F"],
    "violencia": ["\U0001F92C", "\U0001F44A", "\U0001F52D"],
    "ridiculo": ["\U0001F921"]
}

# Limpieza y Lematización
def limpiarTexto(texto):
    if not isinstance(texto, str):
        return ""
    texto = texto.lower().replace("\n", " ").replace("\r", " ")
    texto = re.sub(r'[áàäâ]', 'a', texto)
    texto = re.sub(r'[éèëê]', 'e', texto)
    texto = re.sub(r'[íìïî]', 'i', texto)
    texto = re.sub(r'[óòöô]', 'o', texto)
    texto = re.sub(r'[úùüû]', 'u', texto)
    texto = re.sub(r'\d+', '', texto)
    texto = re.sub(r'\bja(j|a)*\b', ' reir ', texto)
    texto = re.sub(r'\bha(ha)+\b', ' reir ', texto)
    for abr, full in term_estandar.items():
        texto = re.sub(r'\b' + re.escape(abr) + r'\b', ' ' + full + ' ', texto)
    for etiqueta, lista_simbolos in terminosEmoji.items():
        for simbolo in lista_simbolos:
            texto = texto.replace(simbolo, ' ' + etiqueta + ' ')
    puntuacion_completa = string.punctuation + '“”‘’«»—–…¿¡'
    patron_puntuacion = f"[{re.escape(puntuacion_completa)}]"
    texto = re.sub(patron_puntuacion, " ", texto)
    palabras = texto.split()
    filtradas = [w for w in palabras if w not in stop_words and len(w) > 1]
    return " ".join(filtradas)

def lematizarTexto(texto):
    if not texto:
        return ""
    doc = nlp(texto)
    lemmas = [tok.lemma_ for tok in doc]
    return " ".join(lemmas)

# Cargar modelo y vectorizador TF-IDF
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "model", "ModeloClasificador.pkl")
VECTORIZER_PATH = os.path.join(BASE_DIR, "model", "tfidf_vectorizador.pkl")

with open(MODEL_PATH, "rb") as f:
    model = pickle.load(f)

with open(VECTORIZER_PATH, "rb") as f:
    vectorizer = pickle.load(f)

# Configuración de FastAPI
app = FastAPI(title="Detección de Violencia en Relatos")

class TextoEntrada(BaseModel):
    texto: str

class ClasificacionSalida(BaseModel):
    isViolence: bool

@app.post("/classify", response_model=ClasificacionSalida)
async def classify(data: TextoEntrada):
    try:
        # Limpiar y lematizar el texto
        texto_limpio = limpiarTexto(data.texto)
        texto_lematizado = lematizarTexto(texto_limpio)
        # Transformar a vector TF-IDF
        vector = vectorizer.transform([texto_lematizado])
        # Predicción
        pred = model.predict(vector)[0]
        is_violence = bool(pred == 1)

        return ClasificacionSalida(isViolence=is_violence)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
async def health():
    return {"status": "ok"}