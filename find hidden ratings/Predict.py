
import logging

from gensim.models import LdaModel
from gensim import corpora
import nltk
from nltk.stem.wordnet import WordNetLemmatizer
from stop_words import get_stop_words
import json

# Adjust the topics for different training model (Adjust each time when run Train.py)
aspect = {1:"food", 2:"ambient", 3:"service", 4:"location", 5:"price"}
topics = {1:aspect[1], 2:aspect[1], 3:aspect[4], 4:aspect[1], 5:aspect[2], 6:aspect[2], 7:aspect[3],\
          8:aspect[4], 9:aspect[1], 10:aspect[1], 11:aspect[2], 12:aspect[1], 13:aspect[1], \
          14:aspect[1], 15:aspect[3], 16:aspect[5], 17:aspect[5], 18:aspect[3], 20:aspect[1],\
          21:aspect[1], 22:aspect[3], 23:aspect[2], 24:aspect[2], 25:aspect[1], 26:aspect[1],\
          27:aspect[2], 29:aspect[1], 30:aspect[3], 32:aspect[2], 33:aspect[1], 34:aspect[5],\
          35:aspect[1], 36:aspect[1], 37:aspect[1], 38:aspect[1], 39:aspect[1], 40:aspect[3],\
          41:aspect[1], 42:aspect[5], 43:aspect[3], 44:aspect[1], 45:aspect[3], 46:aspect[1],\
          47:aspect[2], 48:aspect[2], 49:aspect[4]}
ratingSum = {"food":0.0, "ambient":0.0, "service": 0.0, "location": 0.0, "price": 0.0}
denominator = {"food":0.0, "ambient":0.0, "service": 0.0, "location": 0.0, "price": 0.0}

def main():
    with open('data/reviewAndRateOfOneRestaurant.json') as data_file:
        content = data_file.readlines();

    for line in content:
        reviewItem = json.loads(line)
        new_review = reviewItem["text"]
        rate = reviewItem["rate"]
        run(new_review, rate)


    for i in ratingSum:
        print i
        print ratingSum[i] / denominator[i]


def run(new_review, rate):
    tokenizer = nltk.RegexpTokenizer(r'\w+')

    en_stop = get_stop_words('en')

    p_stemmer = nltk.PorterStemmer()
    letters_only = nltk.re.sub("[^a-zA-Z]", " ", new_review)
    raw = letters_only.lower()
    tokens = tokenizer.tokenize(raw)

    words = []
    tagged_text = nltk.pos_tag(tokens)
    for word, tag in tagged_text:
        words.append({"word": word, "pos": tag})

    lem = WordNetLemmatizer()
    nouns = []
    for word in words:
        if word["pos"] in ["NN", "NNS"]:
            nouns.append(lem.lemmatize(word["word"]))

    stopped_tokens = [i for i in nouns if not i in en_stop]

    stemmed_tokens = [p_stemmer.stem(i) for i in stopped_tokens]

    dictionary = corpora.Dictionary.load("dictionary.dict")
    lda = LdaModel.load("lda_model_50_topics.lda")

    new_review_bow = dictionary.doc2bow(stemmed_tokens)
    new_review_lda = lda[new_review_bow]

    for i in new_review_lda:
        topic = i[0]
        probability = i[1]
        if topic in topics:
            topicName = topics.get(topic)
            ratingSum[topicName] = ratingSum.get(topicName) + probability * rate
            denominator[topicName] = denominator.get(topicName) + probability

if __name__ == '__main__':
    main()
