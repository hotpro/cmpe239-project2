import nltk
from nltk import WordNetLemmatizer
from nltk.tokenize import RegexpTokenizer
from stop_words import get_stop_words
from nltk.stem.porter import PorterStemmer
from gensim import corpora, models
import gensim
import re

tokenizer = RegexpTokenizer(r'\w+')

# stop words list
en_stop = get_stop_words('en')

p_stemmer = PorterStemmer()

with open('data/PhoenixReview.txt') as data_file:
    doc_set = data_file.read().decode('utf-8', 'ignore').splitlines()

texts = []

for i in doc_set:
    letters_only = re.sub("[^a-zA-Z]",  " ", i)

    raw = letters_only.lower()
    tokens = tokenizer.tokenize(raw)

    stopped_tokens = [i for i in tokens if not i in en_stop]

    words = []
    tagged_text = nltk.pos_tag(stopped_tokens)
    for word, tag in tagged_text:
        words.append({"word": word, "pos": tag})

    lem = WordNetLemmatizer()
    nouns = []
    for word in words:
        if word["pos"] in ["NN", "NNS", "RB", "VBD", "VBN", "JJ"]:
            nouns.append(lem.lemmatize(word["word"]))

    stemmed_tokens = [p_stemmer.stem(i) for i in nouns]

    texts.append(stemmed_tokens)

dictionary = corpora.Dictionary(texts)
dictionary.filter_extremes(keep_n=10000)

corpus = [dictionary.doc2bow(text) for text in texts]

ldamodel = gensim.models.ldamodel.LdaModel(corpus, num_topics=50, id2word = dictionary, passes = 20)
print(ldamodel.print_topics(num_topics=50, num_words=6))
print "bound: "
print ldamodel.bound(corpus, gamma=None, subsample_ratio=1.0)
ldamodel.save("lda_model_50_topics.lda")
corpora.Dictionary.save(dictionary, "dictionary.dict")