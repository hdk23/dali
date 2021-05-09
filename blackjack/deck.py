from random import shuffle
from card import Card
suits = ['♣', '♦', '♥', '♠']
values = ['A', 2, 3, 4, 5, 6, 7, 8, 9, 10, 'J', 'Q', 'K']


class Deck:
    """Deck class that represents a card deck"""
    def __init__(self, num_decks=1):
        self.cards = []
        for num in range(num_decks):
            self.add_standard_deck()
        shuffle(self.cards)

    def add_standard_deck(self):
        """adds a standard deck to the deck of cards"""
        for suit in suits:
            for value in values:
                self.cards.append(Card(suit, value))

    def remove_card(self):
        """removes a card from the deck"""
        card = self.cards[0]
        del self.cards[0]
        return card

    def __str__(self):
        string = "Deck: "
        for card in self.cards:
            string += f"{card} "
        return string
