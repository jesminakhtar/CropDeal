import {
  Component,
  computed,
  OnInit,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';

import { InventoryService } from '../../../../core/services/inventory-service';
import { Product } from '../../../../core/models/inventory.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-marketplace',
  imports: [
    FormsModule,
    DecimalPipe,
    RouterLink
  ],
  templateUrl: './marketplace.html',
  styleUrl: './marketplace.scss'
})
export class MarketplaceComponent implements OnInit {

  loading = signal(true);

  errorMessage = signal('');

  products = signal<Product[]>([]);

  searchTerm = signal('');

  selectedCategory = signal('All');


  categories = computed(() => {

    const values =
      this.products()
        .map(product => product.category)
        .filter(Boolean);

    return [
      'All',
      ...Array.from(new Set(values))
    ];
  });


  filteredProducts = computed(() => {

    const search =
      this.searchTerm()
        .trim()
        .toLowerCase();

    const category =
      this.selectedCategory();


    return this.products().filter(product => {

      const matchesSearch =
        !search ||
        product.name
          .toLowerCase()
          .includes(search) ||
        product.description
          ?.toLowerCase()
          .includes(search);

      const matchesCategory =
        category === 'All' ||
        product.category === category;

      return (
        matchesSearch &&
        matchesCategory
      );
    });
  });


  constructor(
    private inventoryService: InventoryService
  ) {}


  ngOnInit(): void {
    this.loadProducts();
  }


  loadProducts(): void {

    this.loading.set(true);

    this.errorMessage.set('');


    this.inventoryService
      .getAllProducts()
      .subscribe({

        next: products => {

          console.log(
            'Marketplace products:',
            products
          );

          this.products.set(
            products ?? []
          );

          this.loading.set(false);
        },


        error: (error: any) => {

          console.error(
            'Failed to load marketplace:',
            error
          );

          this.errorMessage.set(
            'Unable to load marketplace products right now.'
          );

          this.loading.set(false);
        }

      });
  }


  clearFilters(): void {
    this.searchTerm.set('');
    this.selectedCategory.set('All');
  }
}