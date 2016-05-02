import sys
from load_samples import load_samples
from load_samples import stopword_filtered_bigrams
from naive_bayes import naive_bayes
from naive_bayes import find_fake_review


def main(argv):
    test(argv)
    # apply_to_real(argv)

""" Find fake review in 40000 reviews"""
def apply_to_real(argv):
    filtr = stopword_filtered_bigrams
    try:
        (pos_words, neg_words) = load_samples("Restaurants", 1000, filtr)
    except Exception:
        print("The data for this category and quantity are not found.")
        sys.exit(2)
    find_fake_review(pos_words, neg_words, filtr)

""" Training ans evaluate classifer using k-fold cross method in 20000 neg reviews and 20000 pos reviews"""
def test(argv):
    if len(argv) != 3:
        print 'usage: python run_bayes <category> <quantity> <nfolds>'
        sys.exit(2)
    category = argv[0]
    quantity = int(argv[1])
    n_folds = int(argv[2])

    print "Category: '%s'\n" % category
    filters = [stopword_filtered_bigrams]

    for filt in filters:
        print "Filter: ", filt.__name__
        print "%s-fold stratified cross-validation on %s samples" % (n_folds, quantity * 2)

        try:
            (pos_words, neg_words) = load_samples(category, quantity, filt)
        except Exception:
            print("The data for this category and quantity are not found.")
            sys.exit(2)

        (accuracy, classifier, train_set, test_set) = naive_bayes(pos_words, neg_words, n_folds)
        print "accuracy: %s\n" % accuracy
        classifier.show_most_informative_features()
        print "\n"

if __name__ == "__main__":
   main(sys.argv[1:])