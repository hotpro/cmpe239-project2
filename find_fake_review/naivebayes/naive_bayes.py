import nltk.classify.util
from nltk.classify import NaiveBayesClassifier
from sklearn import cross_validation
import numpy as np

def naive_bayes(pos_samples, neg_samples, n_folds = 2):
    '''Trains a naive bayes classifier with NLTK. It uses stratified 
    n-fold validation. Inputs are the positive and negative samples and 
    the number of folds. Returns the total accuracy and the classifier and 
    the train/test sets of the last fold.'''
    samples = np.array(pos_samples + neg_samples)
    labels = [label for (words, label) in samples]
    cv = cross_validation.StratifiedKFold(labels, n_folds= n_folds, shuffle=True)
    
    accuracy = 0.0
    for traincv, testcv in cv:

        train_samples = samples[traincv]
        test_samples = samples[testcv]
        classifier = nltk.NaiveBayesClassifier.train(train_samples)
        accuracy += nltk.classify.util.accuracy(classifier, test_samples)
    accuracy /= n_folds
    return (accuracy, classifier, train_samples, test_samples)

review_file_name = "/Users/yutao/project_b/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review_restaurant_40000.json"
def find_fake_review(pos_samples, neg_samples, filtr):
    import json
    samples = np.array(pos_samples + neg_samples)
    classifier = nltk.NaiveBayesClassifier.train(samples)
    with open(review_file_name, "r") as review_file:
        for line in review_file:
            line = json.loads(line)
            (review_text, stars) = (line["text"], line["stars"])
            filtered_review = filtr(review_text)
            class1 = classifier.classify(filtered_review)
            if class1 == "pos" and stars <= 1:
                print "=================================Found fake review================================="
                print "stars: %s, is_positive: %s" % (stars, class1)
                if len(review_text) > 100:
                    print "review: " + review_text[:1000]
                else:
                    print "review: " + review_text
            if class1 == "neg" and stars >= 5:
                print "=================================Found fake review================================="
                print "stars: %s, is_positive: %s" % (stars, class1)
                if len(review_text) > 100:
                    print "review: " + review_text[:1000]
                else:
                    print "review: " + review_text