# Author: Henry Kim
# Purpose: Blackjack game made for fun
from deck import Deck
from hand import Hand


class Blackjack:
    """Blackjack class that implements a Blackjack game"""
    def __init__(self, num_decks=1):
        self.deck = Deck(num_decks)
        self.hands = {}  # key: value    player name: hand

        # receive player names
        name = input("Enter player name: ")
        while name != "quit":
            self.hands[name] = Hand()
            name = input("Enter player name: ")
        print()

        # create dealer hand
        self.hands["Dealer"] = Hand()

        # set each player's hand
        self.set_hands()

    def set_hands(self):
        """initializes each player's hand to start with two cards"""
        for key in self.hands.keys():
            self.hands[key].cards.append(self.draw_card())
            self.hands[key].cards.append(self.draw_card())
        return self.hands

    def draw_card(self):
        """removes and returns a card from the deck"""
        return self.deck.remove_card()

    def show_hands(self):
        """shows each player's hand with one of the dealer's cards obscured"""
        print("Current Hands")
        for key, value in self.hands.items():
            if key != "Dealer":
                print(f"{key}: {value}, Total: {value.score()}")
            else:
                print(f"{key}: {value.__str__()[:-1]}, ??")
        print()

    def gameplay(self):
        """executes gameplay"""
        # start by showing each player's hand
        self.show_hands()

        if self.blackjack():
            return self.blackjack()

        # gives each player a turn
        for player in self.hands:
            if player != "Dealer":
                print(f"{player}: Current Score {self.hands[player].score()}")
                move = input("Enter 'h' for hit and 's' for stay. ")
                while move == 'h' and self.hands[player].score() < 21:
                    self.hands[player].player_move(self.draw_card())
                    if self.hands[player].score() >= 21:
                        break
                    move = input("Enter 'h' for hit and 's' for stay. ")
                print(f"Final Score: {self.hands[player].score()}")
                print()

    def blackjack(self, winning_score=21):
        """determines the winner(s) of the round"""
        winners = []
        for key in self.hands.keys():
            if self.hands[key].score() == winning_score:
                winners.append(key)
        if len(winners) > 0:
            return f"{winners} win(s)!"
        return None

    def results(self):
        """returns the results of the game"""
        print("Dealer's Turn")
        print(self.hands["Dealer"])
        while self.hands["Dealer"].score() < 16:
            self.hands["Dealer"].player_move(self.draw_card())

        winning_score = 21
        while not self.blackjack(winning_score):
            winning_score -= 1
        return self.blackjack(winning_score)


# Test Code
blackjack = Blackjack(6)  # initialize a Blackjack deck with six standard decks of playing cards
blackjack.gameplay()  # execute gameplay
print(blackjack.results())  # print the results of the round
