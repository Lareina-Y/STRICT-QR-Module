import numpy as np
import os

from gensim.models import Word2Vec 
import gensim.downloader as api
v2w_model = api.load('word2vec-google-news-300')

# repoNames = ["ecf", "eclipse.jdt.core", "eclipse.jdt.debug", "eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70"]
repoNames = ["ecf", "eclipse.jdt.core", "eclipse.jdt.debug", "eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70",
             "adempiere-3.1.0", "apache-nutch-1.8", "apache-nutch-2.1", "atunes-1.10.0", "bookkeeper-4.1.0", 
             "commons-math-3-3.0", "derby-10.9.1.0", "eclipse-2.0", "jedit-4.2", "lang", "mahout-0.4", 
             "mahout-0.8", "math", "openjpa-2.0.1", "tika-1.3", "time"]

packageName = "STRICT-Replication-Package"

# def load_embedding(file_path):
#     embeddings_index = {}
#     with open(file_path, encoding='utf-8') as f:
#         for line in f:
#             values = line.split()
#             word = values[0]
#             coefs = np.asarray(values[1:], dtype='float32')
#             embeddings_index[word] = coefs
#     return embeddings_index

# # Path to the GloVe file
# glove_file_path = "STRICT-Replication-Package/GloVe/glove.6B/glove.6B.100d.txt"

# # Load the embeddings
# glove_model = load_embedding(glove_file_path)

for repoName in repoNames:
    folder_path = f"{packageName}/ChangeReqs-Normalized/{repoName}"
    output_folder_path = f"{packageName}/Word2Vec/ChangeReqs-Normalized-Emb/{repoName}"
    for filename in os.listdir(folder_path):
        if filename.endswith('.txt'):  # Assuming all files are text files
            file_path = os.path.join(folder_path, filename)
            with open(file_path, 'r') as file:
                lines = file.readlines()

            # Tokenize and encode each token in each sentence
            save_file_path = output_folder_path + f"/{filename}"
            with open(save_file_path, "w") as f:
                for line in lines:
                    if line != '\n':  # Skip empty lines
                        sentence = line.strip()  # Remove leading/trailing whitespaces
                        tokens = sentence.split()  # Split by whitespace

                        for token in tokens:
                            # tokenLowcase = token.lower() # To lowercase
                            tokenLowcase = token
                            if tokenLowcase in v2w_model:
                                embedding = v2w_model[tokenLowcase]
                                embedding_str = " ".join([str(value) for value in list(embedding)])
                            else:
                                embedding_str = ""

                            # Write word and its embedding to a file
                            # embedding_str = " ".join([str(value) for value in list(embedding)])
                            f.write(f"{token}\t{embedding_str}\n")
                        # Add an empty line after each sentence for separation
                        f.write('\n')

        print("Saved: " + repoName + "-" + filename)

# import gensim.downloader as api
# glove_model = api.load('glove-twitter-25')
# print(glove_model['computer'])

# from gensim.models import Word2Vec 
# import gensim.downloader as api
# v2w_model = api.load('word2vec-google-news-300')
# print(v2w_model['check'])

# from sentence_transformers import SentenceTransformer
# model = SentenceTransformer('sentence-transformers/all-mpnet-base-v2')
# print(model.encode('ProductionLines'))