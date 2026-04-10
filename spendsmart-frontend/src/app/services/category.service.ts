import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Category } from '../models/category.model';

/**
 * Service for spending category operations.
 * Retrieves categories for use in expense and budget dropdowns.
 */
@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  /** Base URL for category API endpoints */
  private readonly categoryUrl = `${environment.apiUrl}/api/categories`;

  constructor(private http: HttpClient) {}

  /**
   * Retrieve all available spending categories.
   * Used to populate category selection dropdowns in forms.
   *
   * @returns Observable with all categories
   */
  getAllCategories(): Observable<ApiResponse<Category[]>> {
    return this.http.get<ApiResponse<Category[]>>(this.categoryUrl);
  }
}
