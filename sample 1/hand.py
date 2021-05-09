from card import Card
aces = ["A ♣", "A ♦", "A ♥", "A ♠"]
faces = {"J": 10, "Q": 10, "K": 10, "A": 11}


class Hand:
    """Hand class that represents a player's hand"""
    def __init__(self):
        self.cards = []

    def ace_count(self):
        """returns the number of aces in a player's hand"""
        count = 0
        for card in self.cards:
            if card.__str__() in aces:
                count += 1
        return count

    def score(self):
        """returns the hand's blackjack score"""
        total = 0
        for card in self.cards:
            if card.value in faces:
                total += faces[card.value]
            else:
                total += card.value
        player_aces = self.ace_count()
        while total > 21 and player_aces:
            total -= 10
            player_aces -= 1
        return total

    def player_move(self, card: Card):
        """adds the drawn card to the player's deck and calculates the new score"""
        self.cards.append(card)
        print(self.__str__())
        total = self.score()
        if total > 21:
            print("Busted!")
        elif total == 21:
            print("Blackjack!")
        else:
            print(f"Score: {total}")
        print()

    def __str__(self):
        string = "Hand: "
        for card in self.cards:
            string += f"{card} "
        return string
